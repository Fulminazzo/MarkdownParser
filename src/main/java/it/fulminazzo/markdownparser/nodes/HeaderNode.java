package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.Tag;
import it.fulminazzo.markdownparser.objects.ContentMap;
import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class HeaderNode extends Node {
    protected int header;
    protected String headerText;

    public HeaderNode() {

    }

    public HeaderNode(String text) {
        setContent(text);
    }

    public void setContent(String rawText) {
        if (rawText == null) return;
        rawText = Tag.parseRawText(rawText);
        for (Tag tag : Tag.getHeaderValues()) {
            Matcher matcher = Pattern.compile(tag.getTagsRegex()).matcher(rawText);
            if (matcher.find()) {
                rawText = rawText.substring(matcher.group().length());
                String contents = tag.unParse(matcher.group(1));
                matcher = Pattern.compile(tag.getRegex()).matcher(contents);
                if (matcher.find()) {
                    header = matcher.group(1).length();
                    headerText = matcher.group(2);
                    if (matcher.groupCount() > 2) addChildNode(matcher.group(3));
                }
                if (!rawText.isEmpty()) addNode(rawText);
                return;
            }
        }
    }

    @Override
    protected ContentMap getContentMap() {
        return super.getContentMap().set("header-size", String.valueOf(header),
                "header", headerText);
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && headerText == null;
    }

    @Override
    public String serialize() {
        String serialize = serializeChildren();
        if (serialize == null) serialize = "";
        serialize = String.format("%s %s\n", "#".repeat(header), headerText == null ? "" : headerText) + serialize;
        return serialize;
    }
}
