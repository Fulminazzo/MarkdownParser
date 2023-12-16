package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.Tag;
import it.fulminazzo.markdownparser.utils.Base64Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TagNode extends Node {
    protected final Tag[] tags;

    public TagNode(Tag... tags) {
        this(null, tags);
    }

    public TagNode(String rawText, Tag... tags) {
        this.tags = tags;
        if (rawText != null) setContent(rawText);
    }

    public void setContent(String rawText) {
        if (rawText == null) return;
        for (Tag tag : tags) {
            Matcher matcher = Pattern.compile(tag.getTagsRegex()).matcher(rawText);
            if (matcher.find()) {
                String match = matcher.group(1);
                try {
                    rawText = Base64Utils.decode(match);
                } catch (Exception e) {
                    rawText = match;
                }
                break;
            }
        }
        setContents(rawText);
    }

    protected abstract void setContents(String rawText);
}
