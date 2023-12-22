package it.fulminazzo.markdownparser.utils;

import it.fulminazzo.markdownparser.enums.Tag;
import it.fulminazzo.markdownparser.nodes.HeaderNode;
import it.fulminazzo.markdownparser.nodes.Node;
import it.fulminazzo.markdownparser.nodes.TextBlock;
import it.fulminazzo.markdownparser.nodes.TextNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A collection of functions for working with nodes.
 */
public class NodeUtils {

    /**
     * Formats a raw text and converts it into a node.
     *
     * @param rawText the raw text
     * @return the node
     */
    public static Node formatRawText(String rawText) {
        if (rawText == null) return null;
        rawText = Tag.parseRawText(rawText);
        String[] raw = rawText.split("\n\\s*\n");

        Node mainNode = null;
        for (String text : raw) {
            Node node = null;
            Matcher matcher = Pattern.compile(Constants.TAGS_FINDER_REGEX).matcher(text);
            while (matcher.find()) {
                Tag tag = null;
                try {tag = Tag.valueOf(matcher.group(2));}
                catch (IllegalArgumentException ignored) {}
                String match = matcher.group();
                text = text.substring(match.length());
                String prev = tag == null ? match : matcher.group(1);
                if (!prev.replace("\n", "").isEmpty()) node = createNode(node, formatRawText(prev));
                if (tag != null) {
                    String content = match.substring(prev.length());
                    if (!content.isEmpty()) node = createNode(node, tag.create(content, false));
                }
            }
            if (!text.isEmpty()) node = createNode(node, new TextNode(text));
            Node textBlock;
            if (node instanceof HeaderNode) textBlock = node;
            else {
                textBlock = new TextBlock();
                textBlock.addChildNode(node);
            }
            mainNode = createNode(mainNode, textBlock);
        }
        if (raw.length == 1) mainNode = mainNode.getChild();
        return mainNode;
    }

    /**
     * Repeat the given string for the number of times specified.
     *
     * @param string the string
     * @param times  the times
     * @return the string
     */
    public static String repeat(String string, int times) {
        if (string == null) return null;
        String result = "";
        for (int i = 0; i < times; i++) result += string;
        return result;
    }

    /**
     * Convert an input stream into a string.
     *
     * @param inputStream the input stream
     * @return the string
     * @throws IOException the io exception
     */
    public static String readFromInputStream(InputStream inputStream) throws IOException {
        String output = "";
        while (inputStream.available() > 0 && output.length() < Runtime.getRuntime().freeMemory()) {
            byte[] bytes = new byte[Math.min(2048, inputStream.available())];
            if (inputStream.read(bytes) == -1) throw new IOException();
            output += new String(bytes);
        }
        return output;
    }

    private static Node createNode(Node node, Node newNode) {
        if (node == null) node = newNode;
        else node.addNode(newNode);
        return node;
    }
}