package it.fulminazzo.markdownparser.nodes;

import lombok.Getter;
import lombok.Setter;

/**
 * A Node that represents a simple text.
 */
@Getter
@Setter
public class SimpleTextNode extends Node {
    private String text;

    /**
     * Instantiates a new Simple text node.
     */
    public SimpleTextNode() {
        this(null);
    }

    /**
     * Instantiates a new Simple text node.
     *
     * @param text the text
     */
    public SimpleTextNode(String text) {
        setContent(text);
    }

    /**
     * Sets content.
     *
     * @param text the text
     */
    public void setContent(String text) {
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
