package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.TextType;
import it.fulminazzo.markdownparser.utils.Constants;

public class TextBlock extends TextNode {

    public TextBlock(String text) {
        super(text);
    }

    public TextBlock(String text, TextType textType) {
        super(text, textType);
    }
}
