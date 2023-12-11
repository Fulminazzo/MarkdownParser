package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.utils.NodeUtils;
import it.fulminazzo.markdownparser.utils.Constants;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
public abstract class ContainerNode extends Node {
    protected Node childNode;

    public ContainerNode() {

    }

    public ContainerNode(String rawText) {
        this.childNode = NodeUtils.parseRaw(rawText);
    }

    public void addChildNode(Node node) {
        if (childNode == null) childNode = node;
        else childNode.addNode(node);
    }

    public void removeChildNode(Node node) {
        if (childNode != null) childNode.removeNode(node);
    }

    @Override
    protected LinkedHashMap<String, String> getMapContents() {
        LinkedHashMap<String, String> map = super.getMapContents();
        String output = "{";
        if (childNode != null) output += "\n" + Constants.SEPARATOR +
                childNode.toStringAll().replace("\n", "\n" + Constants.SEPARATOR) + "\n";
        output += "}";
        map.put("children", output);
        return map;
    }

    @Override
    public String serialize() {
        String output = "";
        Node node = childNode;
        while (node != null) {
            output += node.serialize();
            node = node.getNextNode();
        }
        return output;
    }
}
