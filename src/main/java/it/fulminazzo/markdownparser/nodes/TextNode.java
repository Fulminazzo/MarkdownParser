package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.TextType;
import it.fulminazzo.markdownparser.utils.Constants;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedHashMap;

@Getter
public class TextNode extends ContainerNode {
    private TextType textType;

    public TextNode(String text) {
        setText(text, TextType.NORMAL);
    }

    public TextNode(String text, TextType textType) {
        setText(text, textType);
    }

    public void setText(String text) {
        setText(text, TextType.NORMAL);
    }

    public void setText(String text, TextType textType) {
        if (text == null) return;
        if (textType == null) textType = TextType.NORMAL;
        String[] lines = text.split(Constants.TEXT_SEPARATOR);
        text = lines[0];
        this.textType = textType;

        char[] chars = text.toCharArray();
        String textContent = "";
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            textContent += c;

            String res = parseCodeBlock(text.substring(0, i - textContent.length() + 1),
                    text.substring(i + 1),
                    textContent, false);
            if (res == null) {
                if (isStartingCode(textContent)) continue;
                res = parseComment(textContent, false);
            }
            if (res != null) {
                textContent = res;
                continue;
            } else if (isStartingComment(textContent)) continue;
            String innerContent = TextType.convertString(text.substring(i)
                    .replace("<", "<<")
                    .replace(">", ">>")
            );
            for (TextType type : TextType.values()) {
                if (type == TextType.NORMAL) continue;
                String typeTag = String.format("<%s>", type.name());
                if (!innerContent.startsWith(typeTag) || innerContent.startsWith(typeTag + " ")) continue;
                String nodeContent = innerContent.substring(typeTag.length());
                int index = nodeContent.indexOf(typeTag);
                if (index == -1 || nodeContent.indexOf(" " + typeTag) == index - 1) continue;
                nodeContent = nodeContent.substring(0, index);
                if (nodeContent.isEmpty()) continue;
                nodeContent = TextType.unConvertString(nodeContent)
                        .replace("<<", "<")
                        .replace(">>", ">");

                textContent = textContent.substring(0, textContent.length() - 1);
                if (!textContent.isEmpty()) addChildNode(new SimpleTextNode(textContent));
                textContent = "";

                addChildNode(new TextNode(nodeContent, type));

                i += nodeContent.length() + type.getIdChar().length() * 2 - 1;
            }
        }

        if (!textContent.isEmpty() && !textContent.equals("\n")) {
            String res = null;
            if (isStartingCode(textContent))
                res = parseCodeBlock(text.substring(0, text.length() - textContent.length()), "", textContent, true);
            if (res == null) res = parseComment(textContent, true);
            if (res == null || !res.isEmpty()) addChildNode(new SimpleTextNode(textContent));
        }

        if (lines.length > 1) {
            String otherLines = String.join(Constants.TEXT_SEPARATOR, Arrays.copyOfRange(lines, 1, lines.length));
            addNode(new TextBlock(otherLines));
        }
    }

    private String parseCodeBlock(String prev, String next, String textContent, boolean force) {
        if (textContent.contains("\n")) {
            String codeSep = Constants.CODE_SEPARATOR;
            if (textContent.length() <= codeSep.length() * 2) return null;
            String finalTextContent = textContent.substring(0, textContent.length() - codeSep.length());
            String codeTextContent = finalTextContent.substring(codeSep.length());

            if ((textContent.endsWith(codeSep) || force) && !next.equals(codeSep)) {
                if (textContent.startsWith(codeSep)) {
                    String[] tmp = prev.split("\n");
                    prev = prev.endsWith("\n") ? "" : tmp[tmp.length - 1];
                    if (textContent.contains("\n")) {
                        if (prev.matches("^[\t\n\\s]*$")) {
                            if (next.split("\n")[0].matches("^[\t\n\\s]*$")) {
                                if (force) addChildNode(new CodeBlock(textContent));
                                else if (textContent.endsWith("\n" + codeSep))
                                    addChildNode(new CodeBlock(finalTextContent));
                                return "";
                            }
                        } else {
                            addChildNode(new SimpleTextNode(codeSep + finalTextContent.substring(codeSep.length())));
                            return codeSep;
                        }
                    } else {
                        addChildNode(new CodeBlock(codeTextContent));
                        return "";
                    }
                } else if (!force) {
                    addChildNode(new SimpleTextNode(finalTextContent));
                    return codeSep;
                }
            }
        } else if (!force) {
            next = next.isEmpty() ? "" : next.charAt(0) + "";
            for (String codeSep : Constants.getCodeSeparators()) {
                if (textContent.length() <= codeSep.length() * 2) continue;
                String finalTextContent = textContent.substring(0, textContent.length() - codeSep.length());
                String codeTextContent = finalTextContent.substring(codeSep.length());
                if (codeTextContent.replace(codeSep, "").isEmpty()) continue;
                if (next.equals(codeSep)) continue;

                if (textContent.endsWith(codeSep)) {
                    if (textContent.startsWith(codeSep)) {
                        String[] tmp = prev.split("\n");
                        prev = prev.endsWith("\n") ? "" : tmp[tmp.length - 1];
                        if (textContent.contains("\n")) {
                            if (prev.matches("^[\t\n\\s]*$")) {
                                if (textContent.endsWith("\n" + codeSep)) addChildNode(new CodeBlock(finalTextContent));
                                return "";
                            } else {
                                addChildNode(new SimpleTextNode(codeSep + finalTextContent.substring(codeSep.length())));
                                return codeSep;
                            }
                        } else {
                            addChildNode(new CodeBlock(codeTextContent));
                            return "";
                        }
                    } else {
                        addChildNode(new SimpleTextNode(finalTextContent));
                        return codeSep;
                    }
                }
            }
        }
        return null;
    }

    private boolean isStartingCode(String textContent) {
        for (String s : Constants.getCodeSeparators())
            if (textContent.startsWith(s)) return true;
        return false;
    }

    private String parseComment(String textContent, boolean force) {
        for (String[] comment : Constants.getCommentsSeparators()) {
            String cOpening = comment[0];
            String cClosing = comment[1];
            if (textContent.startsWith(cOpening)) {
                if (textContent.endsWith(cClosing) || force) {
                    addChildNode(new CommentNode(textContent));
                    return "";
                }
            } else if (textContent.endsWith(cOpening)) {
                textContent = textContent.substring(0, textContent.length() - cOpening.length());
                if (!textContent.isEmpty()) addChildNode(new SimpleTextNode(textContent));
                return cOpening;
            }
        }
        return null;
    }

    private boolean isStartingComment(String textContent) {
        return Arrays.stream(Constants.getCommentsSeparators()).anyMatch(s -> textContent.startsWith(s[0]));
    }

    @Override
    protected LinkedHashMap<String, String> getMapContents() {
        LinkedHashMap<String, String> map = super.getMapContents();
        if (textType != null) map.put("text-type", textType.toString());
        return map;
    }

    @Override
    public String serialize() {
        return String.format("%s%s%s", textType.getIdChar(), super.serialize(), textType.getIdChar());
    }

    @Override
    public String getContent() {
        return null;
    }
}
