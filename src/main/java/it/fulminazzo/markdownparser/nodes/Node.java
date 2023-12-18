package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.objects.ContentMap;
import it.fulminazzo.markdownparser.objects.NodesList;
import it.fulminazzo.markdownparser.utils.Constants;
import it.fulminazzo.markdownparser.utils.NodeUtils;
import lombok.Getter;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.LinkedList;

@Getter
public abstract class Node {
    protected Node prev;
    protected Node next;
    protected Node parent;
    protected Node child;

    public void addNode(String rawText) {
        if (rawText == null) return;
        addNode(NodeUtils.formatRawText(rawText));
    }

    public void addNode(Node node) {
        if (node == null) return;
        Node last = getLastNode();
        if (node.prev != null) node.prev.next = null;
        if (last.next != null) last.next.prev = null;
        last.next = node;
        node.prev = last;
    }

    public void removeNode(Node node) {
        if (node == null) return;
        if (next == null) return;
        Node prev = next;
        if (next.equals(node)) {
            node.prev = null;
            next = null;
        }
        prev.removeNode(node);
    }

    public Node getNode(Class<? extends Node> nodeClass) {
        Node node = next;
        while (node != null) {
            if (node.getClass().equals(nodeClass)) return node;
            node = node.getNext();
        }
        return null;
    }

    public void addChildNode(String rawText) {
        if (rawText == null) return;
        addChildNode(NodeUtils.formatRawText(rawText));
    }

    public void addChildNode(Node node) {
        if (node == null) return;
        if (node.parent != null) node.parent.removeChildNode(node);
        node.parent = this;
        if (child == null) child = node;
        else child.addNode(node);
    }

    public void removeChildNode(Node node) {
        if (node == null) return;
        if (child == null) return;
        if (child.equals(node)) {
            child.parent = null;
            child = child.next;
        } else {
            Node n = child;
            while (n != null) {
                if (n.equals(node)) {
                    n.parent = null;
                    if (n.prev != null) n.prev.next = n.next;
                    n.prev = null;
                    n.next = null;
                }
                n = n.next;
            }
        }
    }

    public Node getChildNode(Class<? extends Node> nodeClass) {
        Node node = child;
        while (node != null) {
            if (node.getClass().equals(nodeClass)) return node;
            node = node.getNext();
        }
        return null;
    }

    public NodesList getChildren() {
        return new NodesList(child);
    }

    public NodesList findNodes(Class<? extends Node> nodeClass) {
        NodesList nodesList = new NodesList();
        if (this.getClass().equals(nodeClass)) nodesList.add(this);
        if (child != null) nodesList.addAll(child.findNodes(nodeClass));
        if (next != null) nodesList.addAll(next.findNodes(nodeClass));
        return nodesList;
    }

    public Node getFirstNode() {
        if (prev == null) return this;
        else return prev.getFirstNode();
    }

    public Node getLastNode() {
        if (next == null) return this;
        else return next.getLastNode();
    }

    protected ContentMap getContentMap() {
        return new ContentMap();
    }

    protected String serializeChildren() {
        if (child == null) return null;
        String serialize = "";
        Node node = child;
        while (node != null) {
            if (node instanceof HeaderNode) {
                if (!serialize.endsWith("\n")) serialize += "\n";
                if (!serialize.endsWith("\n\n")) serialize += "\n";
            }
            serialize += node.serialize();
            node = node.getNext();
        }
        return serialize;
    }

    public boolean isEmpty() {
        return child == null || child.isEmpty();
    }

    public void write(String file) throws IOException {
        write(new File(file));
    }

    public void write(File file) throws IOException {
        if (file == null) return;
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.mkdirs()) return;
            if (!file.createNewFile()) return;
        }
        write(new FileOutputStream(file));
    }

    public void write(OutputStream outputStream) throws IOException {
        if (outputStream == null) return;
        String serialize = this.serialize();
        if (serialize == null) return;
        outputStream.write(serialize.getBytes());
        outputStream.close();
    }

    public abstract String serialize();

    @Override
    public String toString() {
        String output = String.format("%s: {\n", getClass().getSimpleName());
        LinkedHashMap<String, String> hashMap = getContentMap();
        LinkedList<String> keys = new LinkedList<>(hashMap.keySet());
        for (String key : keys) {
            String value = hashMap.get(key);
            if (value != null) {
                output += String.format("%s: %s", key, value);
                if (!keys.getLast().equalsIgnoreCase(key)) output += ",\n";
            }
        }
        if (child != null) {
            if (!keys.isEmpty()) output += ",\n";
            output += "children: {\n" + Constants.SEPARATOR;
            Node node = child;
            while (node != null) {
                output += node.toString().replace("\n", "\n" + Constants.SEPARATOR);
                node = node.getNext();
                if (node != null) output += ",\n" + Constants.SEPARATOR;
            }
            output += "\n}";
        }
        output = output.replace("\n", "\n" + Constants.SEPARATOR);
        output += "\n}";
        return output;
    }
}