package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.Tag;
import it.fulminazzo.markdownparser.objects.ContentMap;
import it.fulminazzo.markdownparser.utils.Constants;
import lombok.Getter;
import lombok.Setter;

/**
 * A Node that represents a quote block.
 */
@Getter
@Setter
public class QuoteNode extends TagNode {
    private String text;

    /**
     * Instantiates a new Quote node.
     */
    public QuoteNode() {
        this(null);
    }

    /**
     * Instantiates a new Quote node.
     *
     * @param rawContent the raw content
     */
    public QuoteNode(String rawContent) {
        super(rawContent, Tag.getQuoteValues());
    }

    @Override
    protected void setContents(String rawContent) {
        text = rawContent.replaceAll("\n[ \t]*> *", "\n");
    }

    @Override
    public String serialize() {
        String serialize = text.replace("\n", String.format("\n%s ", Constants.QUOTE_SINGLE));
        if (!serialize.endsWith("\n")) serialize += "\n";
        return serialize + "\n";
    }

    @Override
    protected ContentMap getContentMap() {
        return super.getContentMap().set("content", serialize());
    }
}
