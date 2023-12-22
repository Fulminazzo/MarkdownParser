package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.objects.ContentMap;
import it.fulminazzo.markdownparser.objects.NodesList;
import it.fulminazzo.markdownparser.utils.Constants;
import it.fulminazzo.markdownparser.utils.NodeUtils;
import lombok.Getter;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * An abstract Node.
 * This is the base class for every node.
 */
@Getter
public abstract class Node {
    protected Node prev;
    protected Node next;
    protected Node parent;
    protected Node child;

    /**
     * Add node from the given raw content.
     *
     * @param rawContent the raw content
     */
    public void addNode(String rawContent) {
        if (rawContent == null) return;
        addNode(NodeUtils.formatRawText(rawContent));
    }

    /**
     * Add node.
     *
     * @param node the node
     */
    public void addNode(Node node) {
        if (node == null) return;
        Node last = getLastNode();
        if (node.prev != null) node.prev.next = null;
        if (last.next != null) last.next.prev = null;
        last.next = node;
        node.prev = last;
    }

    /**
     * Remove node.
     *
     * @param node the node
     */
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

    /**
     * Gets node from the corresponding class.
     *
     * @param nodeClass the node class
     * @return the node
     */
    public Node getNode(Class<? extends Node> nodeClass) {
        Node node = next;
        while (node != null) {
            if (node.getClass().equals(nodeClass)) return node;
            node = node.getNext();
        }
        return null;
    }

    /**
     * Add child node from the given raw content.
     *
     * @param rawContent the raw content
     */
    public void addChildNode(String rawContent) {
        if (rawContent == null) return;
        addChildNode(NodeUtils.formatRawText(rawContent));
    }

    /**
     * Add child node.
     *
     * @param node the node
     */
    public void addChildNode(Node node) {
        if (node == null) return;
        if (node.parent != null) node.parent.removeChildNode(node);
        node.parent = this;
        if (child == null) child = node;
        else child.addNode(node);
    }

    /**
     * Remove child node.
     *
     * @param node the node
     */
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

    /**
     * Gets child node from the corresponding class.
     *
     * @param nodeClass the node class
     * @return the child node
     */
    public Node getChildNode(Class<? extends Node> nodeClass) {
        Node node = child;
        while (node != null) {
            if (node.getClass().equals(nodeClass)) return node;
            node = node.getNext();
        }
        return null;
    }

    /**
     * Gets children.
     *
     * @return the children
     */
    public NodesList getChildren() {
        return new NodesList(child);
    }

    /**
     * Remove a node recursively, across all children and siblings.
     *
     * @param node the node
     */
    public void removeRecursive(Node node) {
        if (node == null) return;
        removeNode(node);
        removeChildNode(node);
        if (next != null) next.removeRecursive(node);
        if (child != null) child.removeRecursive(node);
    }

    /**
     * Find a list of nodes with the specified class type.
     *
     * @param nodeClass the node class
     * @return the nodes list
     */
    public NodesList findNodes(Class<? extends Node> nodeClass) {
        NodesList nodesList = new NodesList();
        if (this.getClass().equals(nodeClass)) nodesList.add(this);
        if (child != null) nodesList.addAll(child.findNodes(nodeClass));
        if (next != null) nodesList.addAll(next.findNodes(nodeClass));
        return nodesList;
    }

    /**
     * Gets first node.
     *
     * @return the first node
     */
    public Node getFirstNode() {
        if (prev == null) return this;
        else return prev.getFirstNode();
    }

    /**
     * Gets last node.
     *
     * @return the last node
     */
    public Node getLastNode() {
        if (next == null) return this;
        else return next.getLastNode();
    }

    /**
     * Gets the content map.
     * See {@link ContentMap} for more.
     *
     * @return the content map
     */
    protected ContentMap getContentMap() {
        return new ContentMap();
    }

    /**
     * Serialize all children into a string.
     *
     * @return the string
     */
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

    /**
     * Checks if a node is empty or not.
     *
     * @return the boolean
     */
    public boolean isEmpty() {
        return child == null || child.isEmpty();
    }

    /**
     * Calls {@link Node#serialize()} to write to the given file name.
     *
     * @param file the file
     * @throws IOException the io exception
     */
    public void write(String file) throws IOException {
        write(new File(file));
    }

    /**
     * Calls {@link Node#serialize()} to write to the given file.
     *
     * @param file the file
     * @throws IOException the io exception
     */
    public void write(File file) throws IOException {
        if (file == null) return;
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.mkdirs()) return;
            if (!file.createNewFile()) return;
        }
        write(new FileOutputStream(file));
    }

    /**
     * Calls {@link Node#serialize()} to write to the given output stream.
     *
     * @param outputStream the output stream
     * @throws IOException the io exception
     */
    public void write(OutputStream outputStream) throws IOException {
        if (outputStream == null) return;
        String serialize = this.serialize();
        if (serialize == null) return;
        outputStream.write(serialize.getBytes());
        outputStream.close();
    }

    /**
     * Sets content from the given raw string.
     *
     * @param rawContent the raw content
     */
    public abstract void setContent(String rawContent);

    /**
     * Converts the current node to the corresponding Markdown format.
     *
     * @return the string
     */
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