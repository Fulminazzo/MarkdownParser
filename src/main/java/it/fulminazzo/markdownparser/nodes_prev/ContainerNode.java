package it.fulminazzo.markdownparser.nodes_prev;

import it.fulminazzo.markdownparser.utils.NodeUtils_prev;
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
        this.childNode = NodeUtils_prev.parseRaw(rawText);
    }

    public void addChildNode(Node node) {
        if (node == null) return;
        if (childNode == null) childNode = node;
        else childNode.addNode(node);
        childNode.checkNodes();
    }

    public void removeChildNode(Node node) {
        if (node == null) return;
        if (childNode != null) {
            childNode.removeNode(node);
            childNode.checkNodes();
        }
    }
    
    protected void checkChildNodes() {
        childNode = NodeUtils_prev.correctNodes(childNode);
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
            if ((node instanceof TextBlock || node.getNextNode() instanceof TextBlock) && !output.endsWith(Constants.TEXT_SEPARATOR))
                output = output + Constants.TEXT_SEPARATOR;
            node = node.getNextNode();
        }
        return output;
    }
}
