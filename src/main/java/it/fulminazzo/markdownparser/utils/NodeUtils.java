package it.fulminazzo.markdownparser.utils;

import it.fulminazzo.markdownparser.enums.Tag;
import it.fulminazzo.markdownparser.nodes.Node;
import it.fulminazzo.markdownparser.nodes.TextBlock;
import it.fulminazzo.markdownparser.nodes.TextNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NodeUtils {

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
                    String content = matcher.group(3);
                    if (!content.isEmpty()) node = createNode(node, tag.create(content));
                }
            }
            if (!text.isEmpty()) node = createNode(node, new TextNode(text));
            TextBlock textBlock = new TextBlock();
            textBlock.addChildNode(node);
            mainNode = createNode(mainNode, textBlock);
        }
        if (raw.length == 1) mainNode = mainNode.getChild();
        return mainNode;
    }

    public static int getLongestSpace(String string) {
        if (string == null) return -1;
        Matcher matcher = Pattern.compile("( +)").matcher(string);
        int prev = 0;
        while (matcher.find()) prev = Math.max(prev, matcher.group(1).length());
        return prev;
    }

    private static Node createNode(Node node, Node newNode) {
        if (node == null) node = newNode;
        else node.addNode(newNode);
        return node;
    }
}