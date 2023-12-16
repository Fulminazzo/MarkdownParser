package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.TextType;
import it.fulminazzo.markdownparser.utils.Constants;

public class TextBlock extends Node {

    public TextBlock() {

    }

    public TextBlock(String text) {
        this(text, TextType.NORMAL);
    }

    public TextBlock(String text, TextType textType) {
        if (text == null || textType == null) return;
        addChildNode(new TextNode(text, textType));
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
