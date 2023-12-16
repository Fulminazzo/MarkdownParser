package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.Tag;
import it.fulminazzo.markdownparser.objects.ContentMap;

public class QuoteNode extends TagNode {
    private String content;

    public QuoteNode() {
        this(null);
    }

    public QuoteNode(String rawText) {
        super(rawText, Tag.getQuoteValues());
    }

    @Override
    protected void setContents(String rawText) {
        content = rawText.replaceAll("\n[ \t]*> *", "\n");
    }

    @Override
    public String serialize() {
        String serialize = content.replace("\n", "\n> ");
        if (!serialize.endsWith("\n")) serialize += "\n";
        return serialize + "\n";
    }

    @Override
    protected ContentMap getContentMap() {
        return super.getContentMap().set("content", serialize());
    }
}
