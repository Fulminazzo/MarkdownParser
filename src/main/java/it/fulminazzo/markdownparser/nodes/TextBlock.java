package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.TextType;
import it.fulminazzo.markdownparser.utils.Constants;

/**
 * A Node that represents a text block.
 * A text block is a group of strings separated by a pair of new lines.
 */
public class TextBlock extends Node {

    /**
     * Instantiates a new Text block.
     */
    public TextBlock() {

    }

    /**
     * Instantiates a new Text block.
     *
     * @param text the text
     */
    public TextBlock(String text) {
        this(text, TextType.NORMAL);
    }

    /**
     * Instantiates a new Text block.
     *
     * @param text     the text
     * @param textType the text type
     */
    public TextBlock(String text, TextType textType) {
        if (text == null || textType == null) return;
        setContent(text, textType);
    }

    @Override
    public void setContent(String rawContent) {
        setContent(rawContent, TextType.NORMAL);
    }

    /**
     * Sets content.
     *
     * @param rawContent the raw content
     * @param textType   the text type
     */
    public void setContent(String rawContent, TextType textType) {
        addChildNode(new TextNode(rawContent, textType));
    }

    @Override
    public String serialize() {
        String serialize = serializeChildren();
        if (serialize == null) return "";
        serialize = serialize.trim();
        if (!serialize.endsWith("\n")) serialize += "\n";
        if (!serialize.endsWith(Constants.TEXT_SEPARATOR)) serialize += "\n";
        return serialize;
    }
}
