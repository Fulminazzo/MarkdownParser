package it.fulminazzo.markdownparser.nodes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleTextNode extends Node {
    protected String text;

    public SimpleTextNode() {
        this(null);
    }

    public SimpleTextNode(String text) {
        setText(text);
    }

    public void setText(String text) {
        if (text == null) return;
        this.text = text;
    }

    @Override
    public boolean isEmpty() {
        return text == null || text.isEmpty();
    }

    @Override
    public String serialize() {
        return text;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", getClass().getSimpleName(), serialize());
    }
}
