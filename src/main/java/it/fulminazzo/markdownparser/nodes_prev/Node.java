package it.fulminazzo.markdownparser.nodes_prev;

import it.fulminazzo.markdownparser.utils.Constants;
import it.fulminazzo.markdownparser.utils.NodeUtils_prev;
import lombok.Getter;

import java.util.*;

@Getter
public abstract class Node {
    protected Node nextNode;

    public Node getLastNode() {
        return nextNode == null ? this : nextNode.getLastNode();
    }

    public void addNode(Node node) {
        if (node == null) return;
        Node prev = this;
        while (prev.getNextNode() != null) prev = prev.getNextNode();
        if (node.getClass().equals(SimpleTextNode.class) && prev.getClass().equals(node.getClass())) {
            SimpleTextNode nodeText = (SimpleTextNode) node;
            SimpleTextNode prevNode = (SimpleTextNode) prev;
            prevNode.setText(prevNode.text + nodeText.text);
        } else prev.nextNode = node;
        checkNodes();
    }

    public void removeNode(Node node) {
        if (node == null) return;
        if (node.equals(nextNode)) nextNode = nextNode.getNextNode();
        if (nextNode == null) return;
        Node prev = nextNode;
        Node current = nextNode.getNextNode();
        while (current != null) {
            if (current.equals(node)) prev.nextNode = current.getNextNode();
            prev = current;
            current = current.getNextNode();
        }
        checkNodes();
    }

    public void checkNodes() {
        nextNode = NodeUtils_prev.correctNodes(nextNode);
        if (this instanceof ContainerNode) ((ContainerNode) this).checkChildNodes();
    }

    public Node findNode(Class<? extends Node> nodeClass) {
        if (nodeClass == null) return null;
        Node node = this;
        while (node != null && !node.getClass().equals(nodeClass)) node = node.nextNode;
        return node;
    }

    protected LinkedHashMap<String, String> getMapContents() {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put("content", getContent());
        return hashMap;
    }

    public abstract String serialize();

    public abstract String getContent();

    public String toStringAll() {
        String output = this.toString();
        if (nextNode != null) output += ",\n" + nextNode.toStringAll();
        return output;
    }

    @Override
    public String toString() {
        String output = String.format("%s: {", getClass().getSimpleName());
        LinkedHashMap<String, String> hashMap = getMapContents();
        String children = hashMap.remove("children");
        if (children != null) hashMap.put("children", children);
        LinkedList<String> keys = new LinkedList<>(hashMap.keySet());
        for (String key : keys) {
            String value = hashMap.get(key);
            if (value != null) {
                output += String.format("\n%s: %s", key, value);
                if (!keys.getLast().equalsIgnoreCase(key)) output += ",";
            }
        }
        output = output.replace("\n", "\n" + Constants.SEPARATOR);
        output += "\n}";
        return output;
    }
}
