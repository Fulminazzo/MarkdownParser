package it.fulminazzo.markdownparser.objects;

import it.fulminazzo.markdownparser.nodes.Node;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class NodesList extends ArrayList<Node> {

    public NodesList() {

    }

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
