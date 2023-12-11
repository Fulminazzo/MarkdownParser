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

            String res = parseComment(textContent, false);
            if (res != null) {
                textContent = res;
                continue;
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
            String res = parseComment(textContent, true);
            if (res == null || !res.isEmpty()) addChildNode(new SimpleTextNode(textContent));
        }

        if (lines.length > 1) {
            String otherLines = String.join(Constants.TEXT_SEPARATOR, Arrays.copyOfRange(lines, 1, lines.length));
            addNode(new TextBlock(otherLines));
        }
    }

    private String parseComment(String textContent, boolean force) {
        for (String[] comment : Constants.getCommentsSeparators()) {
            String cOpening = comment[0];
            String cClosing = comment[1];
            if (textContent.endsWith(cOpening)) {
                textContent = textContent.substring(0, textContent.length() - cOpening.length());
                if (!textContent.isEmpty()) addChildNode(new SimpleTextNode(textContent));
                return cOpening;
            } else if (textContent.startsWith(cOpening) && (textContent.endsWith(cClosing) || force)) {
                addChildNode(new CommentNode(textContent));
                return "";
            }
        }
        return null;
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
