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
