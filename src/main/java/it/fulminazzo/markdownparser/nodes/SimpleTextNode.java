package it.fulminazzo.markdownparser.nodes;

import lombok.Setter;

@Setter
public class SimpleTextNode extends Node {
    protected String text;

    public SimpleTextNode(String text) {
        this.text = text;
    }

    @Override
    public String serialize() {
        return text;
    }

    @Override
    public String getContent() {
        return text;
    }
}
