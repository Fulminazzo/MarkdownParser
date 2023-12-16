package it.fulminazzo.markdownparser.utils;

import it.fulminazzo.markdownparser.enums.Tag;
import it.fulminazzo.markdownparser.nodes.Node;
import it.fulminazzo.markdownparser.nodes.TextBlock;
import it.fulminazzo.markdownparser.nodes.TextNode;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NodeUtils {

    public static Node formatRawText(String rawText) {
        if (rawText == null) return null;
        rawText = Tag.parseRawText(rawText);
        String[] raw = rawText.split(Constants.TEXT_SEPARATOR);

        Node mainNode = null;
        for (String text : raw) {
            Node node = null;
            Tag[] tags = Tag.values();
            for (int i = 0; i < tags.length; i++) {
                Tag tag = tags[i];
                Matcher matcher = Pattern.compile(Constants.getTagsRegex(tag)).matcher(text);
                if (matcher.find()) {
                    String match = matcher.group();
                    text = text.substring(match.length());
                    String prev = matcher.group(1);
                    if (!prev.replace("\n", "").isEmpty()) node = createNode(node, formatRawText(prev));
                    String content = matcher.group(2);
                    if (!content.isEmpty()) node = createNode(node, tag.create(content));
                    i = 0;
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

    private static Node createNode(Node node, Node newNode) {
        if (node == null) node = newNode;
        else node.addNode(newNode);
        return node;
    }
}