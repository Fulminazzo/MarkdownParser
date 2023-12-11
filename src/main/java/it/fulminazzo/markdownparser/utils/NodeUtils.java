package it.fulminazzo.markdownparser.utils;

import it.fulminazzo.markdownparser.nodes.Node;
import it.fulminazzo.markdownparser.nodes.TextBlock;
import it.fulminazzo.markdownparser.nodes.TextNode;

import java.util.Stack;

public class NodeUtils {

    public static Node parseRaw(String rawText) {
        if (rawText == null) return null;
        /*Node firstNode = new TextNode("");

        String tmp = "";
        for (char c : rawText.toCharArray())
            tmp = parseRawText(firstNode, tmp + c);

        if (!tmp.endsWith("\n\n")) tmp += "\n\n";
        parseRawText(firstNode, tmp);*/

        return new TextBlock(rawText);
    }

    private static String parseRawText(Node node, String rawText) {
        if (rawText.endsWith("\n\n")) {
            node.addNode(new TextNode(rawText));
            rawText = "";
        }
        return rawText;
    }
}