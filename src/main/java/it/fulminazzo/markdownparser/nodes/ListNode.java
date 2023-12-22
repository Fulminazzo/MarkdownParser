package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.Tag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Node that represents a list block.
 */
public class ListNode extends TagNode {

    /**
     * Instantiates a new List node.
     */
    public ListNode() {
        this(null);
    }

    /**
     * Instantiates a new List node.
     *
     * @param rawContent the raw content
     */
    public ListNode(String rawContent) {
        super(rawContent, Tag.LIST);
    }

    @Override
    protected void setContents(String rawContent) {
        Matcher matcher = Pattern.compile(Tag.LIST.getRegex()).matcher(rawContent);
        if (!matcher.find()) return;
        String[] subNodes = rawContent.split("(^|\n) ?-");
        for (String node : subNodes) {
            if (node.isEmpty()) continue;
            node = node.replaceAll("\n {0,4}-", "\n-");
            addChildNode(new ListElement(node));
        }
    }

    @Override
    public String serialize() {
        if (child == null) return null;
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
                }
                childNode = childNode.getNext();
            }
            if (!serialize.endsWith("\n")) serialize += "\n";
            node = node.getNext();
        }
        return serialize;
    }
}