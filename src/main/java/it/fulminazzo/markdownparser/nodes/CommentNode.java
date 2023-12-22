package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.Tag;
import it.fulminazzo.markdownparser.objects.ContentMap;
import it.fulminazzo.markdownparser.utils.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Node that represents a comment block.
 */
public class CommentNode extends TagNode {
    private String text;

    /**
     * Instantiates a new Comment node.
     */
    public CommentNode() {
        this(null);
    }

    /**
     * Instantiates a new Comment node.
     *
     * @param rawContent the raw content
     */
    public CommentNode(String rawContent) {
        super(rawContent, Tag.getCommentValues());
    }

    @Override
    protected void setContents(String rawContent) {
        for (Tag tag : tags) {
            Matcher matcher = Pattern.compile(tag.getRegex()).matcher(rawContent);
            if (matcher.find()) {
                text = matcher.group(1);
                return;
            }
        }
    }

    @Override
    protected ContentMap getContentMap() {
        return super.getContentMap().set("text", text);
    }

    @Override
    public String serialize() {
        if (text == null) return "";
        String serialize = (text.contains("\n") ? Constants.COMMENT_MULTIPLE : Constants.COMMENT_SINGLE);
        return String.format("\n" + serialize + "\n", text);
    }
}
