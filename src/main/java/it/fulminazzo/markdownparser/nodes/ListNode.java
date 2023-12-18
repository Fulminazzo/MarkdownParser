package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.Tag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListNode extends TagNode {
    private String prev;

    public ListNode() {
        this(null);
    }

    public ListNode(String rawText) {
        super(rawText, Tag.LIST);
    }

    @Override
    protected void setContents(String rawText) {
        Matcher matcher = Pattern.compile(Tag.LIST.getRegex()).matcher(rawText);
        if (!matcher.find()) return;
        prev = matcher.groupCount() > 1 ? matcher.group(2) : "";
        String[] subNodes = rawText.split("(^|\n) ?-");
        for (String node : subNodes) {
            if (node.isEmpty()) continue;
            node = node.replaceAll("\n {0,4}-", "\n-");
            addChildNode(new ListElement(node));
        }
    }

    @Override
    public String serialize() {
        if (prev == null || child == null) return null;
        Node node = child;
        String serialize = "";
        while (node != null) {
            Node childNode = node.getChild();
            serialize += "-";
            while (childNode != null) {
                if (childNode instanceof ListNode) {
                    serialize += "";
                    serialize += String.format("  %s", childNode.serialize()
                            .replace("\n-", "\n  -")
                    );
                    if (serialize.endsWith("\n  ")) serialize = serialize.substring(0, serialize.length() - 2);
                    if (!serialize.endsWith("\n")) serialize += "\n";
                } else {
                    serialize += String.format("%s", childNode.serialize().replace("\n\n", "\n"));
                    //if (!serialize.endsWith("\n")) serialize += "\n";
                }
                childNode = childNode.getNext();
            }
            if (!serialize.endsWith("\n")) serialize += "\n";
            node = node.getNext();
        }
        return serialize;
    }
}