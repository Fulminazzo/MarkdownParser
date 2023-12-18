package it.fulminazzo.markdownparser.nodes;

public class ListElement extends Node {

    public ListElement() {
        this(null);
    }

    public ListElement(String rawText) {
        setText(rawText);
    }

    public void setText(String rawText) {
        if (rawText == null) return;
        while (child != null) removeChildNode(child);
        addChildNode(rawText);
    }

    @Override
    public String serialize() {
        return serializeChildren();
    }
}
