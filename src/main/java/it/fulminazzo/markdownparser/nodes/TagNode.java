package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.Tag;
import it.fulminazzo.markdownparser.utils.Base64Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Node associated with one or more tags ({@link Tag}).
 */
public abstract class TagNode extends Node {
    protected final Tag[] tags;

    /**
     * Instantiates a new Tag node.
     *
     * @param tags the tags
     */
    public TagNode(Tag... tags) {
        this(null, tags);
    }

    /**
     * Instantiates a new Tag node.
     *
     * @param rawText the raw text
     * @param tags    the tags
     */
    public TagNode(String rawText, Tag... tags) {
        this.tags = tags;
        if (rawText != null) setContent(rawText);
    }

    /**
     * Tries to convert the raw content to one of
     * the given tags of the node.
     * If it succeeds, the rawContent will become the
     * decoded from Base64 resulting string inside the
     * tags.
     * Calls {@link TagNode#setContents(String)} to
     * finish the operation.
     *
     * @param rawContent the raw content
     */
    @Override
    public void setContent(String rawContent) {
        if (rawContent == null) return;
        for (Tag tag : tags) {
            Matcher matcher = Pattern.compile(tag.getTagsRegex()).matcher(rawContent);
            if (matcher.find()) {
                String match = matcher.group(1);
                try {
                    rawContent = Base64Utils.decode(match);
                } catch (Exception e) {
                    rawContent = match;
                }
                break;
            }
        }
        setContents(rawContent);
    }

    /**
     * Sets contents.
     *
     * @param rawContent the raw content
     */
    protected abstract void setContents(String rawContent);
}
