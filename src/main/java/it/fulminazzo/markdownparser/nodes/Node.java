package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.utils.Constants;
import lombok.Getter;

import java.util.*;

@Getter
public abstract class Node {
    protected Node nextNode;

    public void addNode(Node node) {
        if (node == null) return;
        Node prev = nextNode;
        if (prev == null) nextNode = node;
        else {
            while (prev.getNextNode() != null) prev = prev.getNextNode();
            prev.nextNode = node;
        }
    }

    public void removeNode(Node node) {
        if (node == null || nextNode == null) return;
        if (node.equals(nextNode)) nextNode = nextNode.getNextNode();
        Node prev = nextNode;
        Node current = nextNode.getNextNode();
        while (current != null) {
            if (current.equals(node)) prev.nextNode = current.getNextNode();
            prev = current;
            current = current.getNextNode();
        }
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
            if (value != null) output += String.format("\n%s: %s", key, value);
            if (!keys.getLast().equalsIgnoreCase(key)) output += ",";
        }
        output = output.replace("\n", "\n" + Constants.SEPARATOR);
        output += "\n}";
        return output;
    }
}
