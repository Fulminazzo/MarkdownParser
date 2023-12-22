package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.Tag;
import it.fulminazzo.markdownparser.enums.TextType;
import it.fulminazzo.markdownparser.objects.ContentMap;
import it.fulminazzo.markdownparser.utils.NodeUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Node that represents a formatted string.
 * A string is formatted if it presents one of the
 * characters specified in {@link TextType}, or if
 * it is empty.
 */
@Getter
@Setter
public class TextNode extends TagNode {
    private TextType textType;

    /**
     * Instantiates a new Text node.
     */
    public TextNode() {
        this(null);
    }

    /**
     * Instantiates a new Text node.
     *
     * @param text the text
     */
    public TextNode(String text) {
        setContent(text);
    }

    /**
     * Instantiates a new Text node.
     *
     * @param text     the text
     * @param textType the text type
     */
    public TextNode(String text, TextType textType) {
        super(Tag.getTextValues());
        setText(text, textType);
    }

    /**
     * Sets text.
     *
     * @param text the text
     */
    public void setText(String text) {
        setText(text, TextType.NORMAL);
    }

    /**
     * Sets text.
     *
     * @param text     the text
     * @param textType the text type
     */
    public void setText(String text, TextType textType) {
        if (text == null) return;
        this.textType = textType;
        setContent(text);
    }

    @Override
    protected void setContents(String rawContent) {
        for (Tag tag : tags) {
            Matcher matcher = Pattern.compile(tag.getRegex()).matcher(rawContent);
            if (matcher.find()) {
                rawContent = matcher.group(1);
                break;
            }
        }
        String text = Tag.parseRawText(rawContent);
        if (Tag.hasValidTag(text)) {
            Node node = NodeUtils.formatRawText(text);
            if (node instanceof TextNode && node.getNext() == null) {
                textType = ((TextNode) node).textType;
                addChildNode(node.getChild());
            } else addChildNode(node);
        }
        else addChildNode(new SimpleTextNode(rawContent));
    }

    @Override
    protected ContentMap getContentMap() {
        return super.getContentMap().set("text-type", textType == null ? null : textType.toString());
    }

    @Override
    public String serialize() {
        String serialize = serializeChildren();
        if (serialize == null) return "";
        if (textType == null) textType = TextType.NORMAL;
        serialize = String.format("%s%s%s", textType.getIdChar(), serialize, textType.getIdChar());
        if (next instanceof ListNode) serialize += "\n";
        return serialize;
    }
}
