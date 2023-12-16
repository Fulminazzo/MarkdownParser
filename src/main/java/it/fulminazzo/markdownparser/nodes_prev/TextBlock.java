package it.fulminazzo.markdownparser.nodes_prev;

import it.fulminazzo.markdownparser.enums.TextType;

public class TextBlock extends TextNode {

    public TextBlock(String text) {
        super(text);
    }

    public TextBlock(String text, TextType textType) {
        super(text, textType);
    }
}
