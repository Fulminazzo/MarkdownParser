package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.TextType;
import it.fulminazzo.markdownparser.utils.Constants;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class TextNode extends ContainerNode {
    private TextType textType;

    public TextNode(String text) {
        setText(text, TextType.NORMAL);
    }

    public TextNode(String text, TextType textType) {
        setText(text, textType);
    }

    public TextNode(String text, TextType textType, boolean checkHeader) {
        setText(text, textType, checkHeader);
    }

    public void setText(String text) {
        setText(text, TextType.NORMAL);
    }

    public void setText(String text, TextType textType) {
        setText(text, textType, true);
    }

    public void setText(String rawText, TextType textType, boolean checkHeader) {
        if (rawText == null) return;
        if (textType == null) textType = TextType.NORMAL;
        rawText = Constants.compressRawText(rawText);
        final String[] lines = rawText.split(Constants.TEXT_SEPARATOR);
        String text = lines[0];
        if (lines.length > 1) text += "\n";
        this.textType = textType;

        char[] chars = text.toCharArray();
        String textContent = "";
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            textContent += c;

            // TABLES
            String next = i == chars.length - 1 ? "" : chars[i + 1] + "";
            String res = parseTable(next, textContent);
            if (res != null && res.isEmpty()) {
                addNode(new TableNode(rawText.substring(i + 1)));
                return;
            }

            // CODE AND COMMENTS
            //String res = parseCodeBlock(i == chars.length - 1 ? "" : chars[i + 1] + "", textContent, false);
            if (res == null) res = parseCodeBlock(next, textContent, false);
            if (res == null) res = parseComment(textContent, false);
            if (res != null) {
                textContent = res;
                continue;
            } else if (isStartingComment(textContent)) continue;

            // HEADERS
            if (checkHeader) {
                //System.out.println(String.format("Content: \"%s\"", Arrays.toString(textContent.split("\n"))));
                String[] t = textContent.split("\n");
                if (t.length > 0 && t[t.length - 1].matches(Constants.HEADER_REGEX)) {
                    addChildNode(new TextNode(String.join("\n", Arrays.copyOfRange(t, 0, t.length - 1))));
                    addNode(new HeaderNode(rawText));
                    return;
                }
            }

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
            textContent += Constants.TEXT_SEPARATOR;
            String res = parseTable("", textContent);
            if (res != null && res.isEmpty()) {
                addNode(new TableNode(rawText.substring(chars.length - textContent.length() + 2)));
                return;
            }
            textContent = textContent.substring(0, textContent.length() - Constants.TEXT_SEPARATOR.length());
            if (res == null) res = parseCodeBlock("", textContent, true);
            if (res == null) res = parseComment(textContent, true);
            if (res == null || !res.isEmpty()) addChildNode(new SimpleTextNode(textContent));
        }

        if (lines.length > 1) {
            String otherLines = String.join(Constants.TEXT_SEPARATOR, Arrays.copyOfRange(lines, 1, lines.length));
            addNode(new TextBlock(otherLines));
        }
    }

    private String parseTable(String next, String textContent) {
        for (int i = 1; i < Constants.MAX_TABLE_LENGTH; i++) {
            String TABLE_REGEX = Constants.getTableRegex(i);
            Pattern tablePattern = Pattern.compile(TABLE_REGEX);
            if (tablePattern.matcher(textContent).find() && !tablePattern.matcher(textContent + next).find()) {
                String[] tmp = textContent.split(TABLE_REGEX);
                if (tmp.length > 0) addChildNode(new TextNode(tmp[0]));
                return "";
            } else {
                TABLE_REGEX = Constants.TABLE_REGEX_BASE64.replace("_N", "_" + i);
                tablePattern = Pattern.compile(TABLE_REGEX);
                Matcher matcher = tablePattern.matcher(textContent);
                if (matcher.find()) {
                    String[] tmp = textContent.split(TABLE_REGEX);
                    if (tmp.length > 0) addChildNode(new TextNode(tmp[0]));
                    return "";
                }
            }
            if (textContent.endsWith("<TABLE_" + i)) {
                addChildNode(new TextNode(textContent.substring(0, textContent.length() - "<TABLE".length() - 1)));
                return "<TABLE_" + i;
            }
            if (textContent.endsWith("<TABLE_")) return "<TABLE_";
        }
        return null;
    }

    private String parseCodeBlock(String next, String textContent, boolean force) {
        if (force) next += "\n";
        Matcher match = Pattern.compile(Constants.CODE_REGEX_BASE64).matcher(textContent + next);
        if (match.find()) {
            String[] tmp = (textContent + next).split(Constants.CODE_REGEX_BASE64);
            if (tmp.length > 0) addChildNode(new SimpleTextNode(tmp[0]));
            addChildNode(new CodeBlock(Constants.decompressCodeBlocks(Constants.formatCodeBlock(match.group(1)))));
            return force ? "" : next;
        }
        match = Pattern.compile(Constants.CODE_REGEX_MULTIPLE_LINES).matcher(textContent + next);
        if (match.find()) {
            String[] tmp = (textContent + next).split(Constants.CODE_REGEX_MULTIPLE_LINES);
            if (tmp.length > 0) addChildNode(new SimpleTextNode(tmp[0]));
            addChildNode(new CodeBlock(match.group(1)));
            return force ? "" : next;
        }
        for (String regex : Arrays.asList(Constants.CODE_REGEX_SINGLE, Constants.CODE_REGEX_MULTIPLE)) {
            Matcher matcher = Pattern.compile(regex).matcher(textContent);
            if (matcher.find()) {
                String[] tmp = textContent.split(regex);
                if (tmp.length > 0) addChildNode(new SimpleTextNode(tmp[0] + matcher.group(1)));
                addChildNode(new CodeBlock(matcher.group(2)));
                return "";
            }
        }
        return null;
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

    public boolean isEmpty() {
        if (childNode == null) return true;
        if (childNode instanceof TextNode) return ((TextNode) childNode).isEmpty();
        if (childNode instanceof SimpleTextNode) return ((SimpleTextNode) childNode).text.isEmpty();
        return false;
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
