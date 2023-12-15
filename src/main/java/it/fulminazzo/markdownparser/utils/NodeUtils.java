package it.fulminazzo.markdownparser.utils;

import it.fulminazzo.markdownparser.enums.TextType;
import it.fulminazzo.markdownparser.nodes.Node;
import it.fulminazzo.markdownparser.nodes.SimpleTextNode;
import it.fulminazzo.markdownparser.nodes.TextBlock;
import it.fulminazzo.markdownparser.nodes.TextNode;

public class NodeUtils {

    public static Node parseRaw(String rawText) {
        if (rawText == null) return null;
        return new TextBlock(rawText);
    }

    public static Node correctNodes(Node node) {
        if (node == null) return null;
        if (node.getClass().equals(SimpleTextNode.class)) {
            SimpleTextNode simpleTextNode = ((SimpleTextNode) node);
            if (simpleTextNode.getText().isEmpty()) node = node.getNextNode();
            else {
                Node nextNode = node.getNextNode();
                if (nextNode != null && (nextNode.getClass().equals(TextNode.class) || nextNode.getClass().equals(TextBlock.class))) {
                    TextNode textNode = ((TextNode) nextNode);
                    if (!textNode.isEmpty() && textNode.getTextType() == TextType.NORMAL) {
                        String finalText = simpleTextNode.getText();
                        if (nextNode.getClass().equals(TextBlock.class)) finalText += Constants.TEXT_SEPARATOR;
                        finalText += textNode.serialize();
                        simpleTextNode.setText(finalText);
                        node.removeNode(textNode);
                    }
                }
            }
        }
        if (node instanceof TextNode) {
            if (((TextNode) node).isEmpty()) node = node.getNextNode();
        }
        if (node != null) node.checkNodes();
        return node;
    }
}