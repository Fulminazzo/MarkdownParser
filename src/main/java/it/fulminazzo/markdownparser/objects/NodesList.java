package it.fulminazzo.markdownparser.objects;

import it.fulminazzo.markdownparser.nodes.Node;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * An implementation of {@link ArrayList}
 * that contains nodes.
 */
public class NodesList extends ArrayList<Node> {

    /**
     * Instantiates a new Nodes list.
     */
    public NodesList() {

    }

    /**
     * Instantiates a new Nodes list.
     *
     * @param node the node
     */
    public NodesList(Node node) {
        if (node == null) return;
        while (node != null) {
            add(node);
            node = node.getNext();
        }
    }

    @Override
    public String toString() {
        return String.format("[%s]", this.stream().map(n -> n == null ? "null" : n.getClass().getName())
                .collect(Collectors.joining(", ")));
    }
}
