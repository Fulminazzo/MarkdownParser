package it.fulminazzo.markdownparser.nodes;

/**
 * A node that represents the element of a list.
 */
public class ListElement extends Node {

    /**
     * Instantiates a new List element.
     */
    public ListElement() {
        this(null);
    }

    /**
     * Instantiates a new List element.
     *
     * @param rawContent the raw content
     */
    public ListElement(String rawContent) {
        setContent(rawContent);
    }
//TODO: More lists
    /**
     * Sets content from a raw text.
     *
     * @param rawContent the raw content
     */
    public void setContent(String rawContent) {
        if (rawContent == null) return;
        while (child != null) removeChildNode(child);
        addChildNode(rawContent);
    }

    @Override
    public String serialize() {
        return serializeChildren();
    }
}
