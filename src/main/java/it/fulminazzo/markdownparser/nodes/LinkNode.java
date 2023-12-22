package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.Tag;
import it.fulminazzo.markdownparser.objects.ContentMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Node that represents a link.
 */
public class LinkNode extends TagNode {
    private String text;
    private String link;
    private String hoverText;

    /**
     * Instantiates a new Link node.
     */
    public LinkNode() {
        this(null);
    }

    /**
     * Instantiates a new Link node.
     *
     * @param rawContent the raw content
     */
    public LinkNode(String rawContent) {
        super(rawContent, Tag.LINK);
    }

    @Override
    protected void setContents(String rawContent) {
        text = null;
        link = null;
        hoverText = null;
        Matcher matcher = Pattern.compile(Tag.LINK.getRegex()).matcher(rawContent);
        if (matcher.matches()) {
            text = matcher.group(1);
            link = matcher.group(2);
            if (matcher.groupCount() > 2) hoverText = matcher.group(3);
        }
    }

    @Override
    protected ContentMap getContentMap() {
        return super.getContentMap().set("text", text, "link", link, "hover-text", hoverText);
    }

    @Override
    public String serialize() {
        if (text == null || link == null) return "";
        return String.format("[%s](%s%s)", text, link,
                hoverText == null ? "" : (" " + hoverText)
        );
    }
}
