package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.Tag;
import it.fulminazzo.markdownparser.objects.ContentMap;
import it.fulminazzo.markdownparser.utils.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentNode extends TagNode {
    private String text;

    public CommentNode() {
        this(null);
    }

    public CommentNode(String rawText) {
        super(rawText, Tag.getCommentValues());
    }

    @Override
    protected void setContents(String rawText) {
        for (Tag tag : tags) {
            Matcher matcher = Pattern.compile(tag.getRegex()).matcher(rawText);
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
