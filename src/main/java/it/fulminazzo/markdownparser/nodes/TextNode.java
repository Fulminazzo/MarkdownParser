package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.Tag;
import it.fulminazzo.markdownparser.enums.TextType;
import it.fulminazzo.markdownparser.objects.ContentMap;
import it.fulminazzo.markdownparser.utils.NodeUtils;
import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class TextNode extends TagNode {
    private TextType textType;

    public TextNode() {
        this(null);
    }

    public TextNode(String text) {
        this(text, TextType.NORMAL);
    }

    public TextNode(String text, TextType textType) {
        super(Tag.getTextValues());
        setText(text, textType);
    }

    public void setText(String text) {
        setText(text, TextType.NORMAL);
    }

    public void setText(String text, TextType textType) {
        if (text == null) return;
        this.textType = textType;
        setContent(text);
    }

    @Override
    protected void setContents(String rawText) {
        for (Tag tag : Tag.getTextValues()) {
            Matcher matcher = Pattern.compile(tag.getRegex()).matcher(rawText);
            if (matcher.find()) {
                rawText = matcher.group(1);
                break;
            }
        }
        String text = Tag.parseRawText(rawText);
        if (Tag.hasValidTag(text)) addChildNode(NodeUtils.formatRawText(text));
        else addChildNode(new SimpleTextNode(rawText));
    }

    public void setText(String rawText, TextType textType, boolean checkHeader) {
        if (rawText == null || textType == null) return;
        this.textType = textType;
        setContents(rawText);;
        /*if (textType != TextType.NORMAL)
            try {
                Tag tag = Tag.valueOf(textType.name());
                rawText = tag.unParse(tag.getUntaggedString(rawText));
                Matcher matcher = Pattern.compile(textType.getRegex()).matcher(rawText);
                if (matcher.find()) rawText = matcher.group(1);
            } catch (IllegalArgumentException ignored) {}
        System.out.println(String.format("Checking if has valid tag: {%s}", rawText));
        if (Tag.hasValidTag(rawText)) addChildNode(NodeUtils.parseRaw(rawText));
        else addChildNode(new SimpleTextNode(rawText));*/
        //Node tmp = NodeUtils.parseRaw(rawText);
        /*if (tmp.getNext() == null && tmp.getChild() == null) addChildNode(new SimpleTextNode(rawText));
        else addChildNode(tmp);*/
        /*if (rawText == null) return;
        if (textType == null) textType = TextType.NORMAL;
        rawText = Tag.parseRawText(rawText);
        final String[] lines = rawText.split(Constants.TEXT_SEPARATOR);
        String text = lines[0];
        if (lines.length > 1) text += "\n";
        this.textType = textType;

        char[] chars = text.toCharArray();
        String textContent = "";
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            textContent += c;

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
            *//*textContent += Constants.TEXT_SEPARATOR;
            String res = parseTable("", textContent);
            if (res != null && res.isEmpty()) {
                addNode(new TableNode(rawText.substring(chars.length - textContent.length() + 2)));
                return;
            }
            textContent = textContent.substring(0, textContent.length() - Constants.TEXT_SEPARATOR.length());
            if (res == null) res = parseCodeBlock("", textContent, true);
            if (res == null) res = parseComment(textContent, true);
            if (res == null || !res.isEmpty())*//* addChildNode(new SimpleTextNode(textContent));
        }

        if (lines.length > 1) {
            String otherLines = String.join(Constants.TEXT_SEPARATOR, Arrays.copyOfRange(lines, 1, lines.length));
            addNode(new TextBlock(otherLines));
        }*/
    }

    /*private String parseTable(String next, String textContent) {
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
    }*/

    @Override
    protected ContentMap getContentMap() {
        return super.getContentMap().set("text-type", textType.toString());
    }

    @Override
    public String serialize() {
        String serialize = serializeChildren();
        if (serialize == null) return "";
        if (textType == null) textType = TextType.NORMAL;
        return String.format("%s%s%s", textType.getIdChar(), serialize, textType.getIdChar());
    }
}
