package it.fulminazzo.markdownparser.enums;

import it.fulminazzo.markdownparser.nodes.*;
import it.fulminazzo.markdownparser.utils.Base64Utils;
import it.fulminazzo.markdownparser.utils.Constants;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public enum Tag {
    // COMMENTS
    COMMENT(Constants.COMMENT_REGEX, CommentNode::new),
    COMMENT2(Constants.COMMENT_REGEX_2, CommentNode::new),
    // QUOTE
    QUOTE(Constants.QUOTE_REGEX, QuoteNode::new),
    // TABLE
    TABLE1(Constants.getTableRegex(1), TableNode::new),
    TABLE2(Constants.getTableRegex(2), TableNode::new),
    TABLE3(Constants.getTableRegex(3), TableNode::new),
    TABLE4(Constants.getTableRegex(4), TableNode::new),
    TABLE5(Constants.getTableRegex(5), TableNode::new),
    TABLE6(Constants.getTableRegex(6), TableNode::new),
    TABLE7(Constants.getTableRegex(7), TableNode::new),
    TABLE8(Constants.getTableRegex(8), TableNode::new),
    TABLE9(Constants.getTableRegex(9), TableNode::new),
    TABLE10(Constants.getTableRegex(10), TableNode::new),
    // CODE
    CODE(Constants.CODE_REGEX_MULTIPLE, CodeNode::new),
    CODE2(Constants.CODE_REGEX_MULTIPLE_LINES, Pattern.MULTILINE, CodeNode::new),
    CODE3(Constants.CODE_REGEX_SINGLE, CodeNode::new),
    // LIST
    LIST(Constants.LIST_REGEX, Pattern.MULTILINE, ListNode::new),
    // LINK
    LINK(Constants.LINK_REGEX, LinkNode::new),
    // TEXT
    STRONG(Constants.STRONG_REGEX, s -> new TextNode(s, TextType.STRONG)),
    STRONG2(Constants.STRONG_REGEX_2, s -> new TextNode(s, TextType.STRONG2)),
    ITALIC(Constants.ITALIC_REGEX, s -> new TextNode(s, TextType.ITALIC)),
    ITALIC2(Constants.ITALIC_REGEX_2, s -> new TextNode(s, TextType.ITALIC2)),
    STRIKETHROUGH(Constants.STRIKETHROUGH, s -> new TextNode(s, TextType.STRIKETHROUGH)),
    // HEADERS
    HEADER1(Constants.getHeaderRegex(1), HeaderNode::new),
    HEADER2(Constants.getHeaderRegex(2), HeaderNode::new),
    HEADER3(Constants.getHeaderRegex(3), HeaderNode::new),
    HEADER4(Constants.getHeaderRegex(4), HeaderNode::new),
    HEADER5(Constants.getHeaderRegex(5), HeaderNode::new),
    HEADER6(Constants.getHeaderRegex(6), HeaderNode::new),
    ;

    private final String regex;
    private final int regexFlags;
    private final String tagName;
    private final Function<String, Node> creator;

    Tag(String regex, Function<String, Node> creator) {
        this(regex, 0, creator);
    }

    Tag(String regex, int regexFlags, Function<String, Node> creator) {
        this.regex = regex;
        this.regexFlags = regexFlags;
        this.creator = creator;
        this.tagName = name();
    }

    public String getTagsRegex() {
        return ".*<" + tagName + ">(.*)</" + tagName + ">.*";
    }

    public String unParse(String rawText) {
        if (rawText == null) return null;
        Matcher matcher = Pattern.compile(rawText).matcher(getTagsRegex());
        if (matcher.find()) rawText = matcher.group(1);
        return Base64Utils.decode(rawText);
    }

    public String getTaggedString(String string) {
        if (string == null) return null;
        return getTagsRegex().replace("(.*)", string).replace(".*", "");
    }

    public String getUntaggedString(String string) {
        if (string == null) return null;
        Matcher matcher = Pattern.compile(getTagsRegex()).matcher(string);
        if (matcher.find()) return matcher.group(1);
        else return string;
    }

    public Node create(String rawText) {
        if (rawText == null || creator == null) return null;
        if (!Pattern.compile(getTagsRegex()).matcher(rawText).find()) rawText = getTaggedString(rawText);
        return creator.apply(rawText);
    }

    public static String parseRawText(String text) {
        if (text == null) return null;
        for (Tag tag : Tag.values()) {
            int regexFlags = tag.getRegexFlags();
            Matcher matcher = (regexFlags > 0 ?
                    Pattern.compile(tag.getRegex(), regexFlags) :
                    Pattern.compile(tag.getRegex())).matcher(text);

            while (matcher.find()) {
                String match = matcher.group();
                String group2 = matcher.groupCount() > 1 ? matcher.group(2) : matcher.group(1);
                try {
                    TextType textType = TextType.valueOf(tag.name());
                    match = textType.getIdChar() + group2 + textType.getIdChar();
                } catch (IllegalArgumentException ignored) {
                    switch (tag) {
                        case CODE2: group2 = matcher.group(1) + "\n" + group2;
                        case CODE: {
                            match = Constants.CODE_SEPARATOR + group2 + Constants.CODE_SEPARATOR;
                            break;
                        }
                        case CODE3: {
                            match = Constants.CODE_SEPARATOR_2 + group2 + Constants.CODE_SEPARATOR_2;
                        }
                    }
                }
                String toReplace = tag.getTaggedString(Base64Utils.encode(match));
                text = text.replace(match, toReplace);
            }
        }
        return text;
    }

    public static boolean hasValidTag(String text) {
        if (text == null) return false;
        for (Tag tag : Tag.values()) {
            Matcher matcher = Pattern.compile(tag.getTagsRegex()).matcher(text);
            if (matcher.find()) return true;
        }
        return false;
    }

    public static Tag[] getCommentValues() {
        return getValues(t -> t.name().startsWith("COMMENT"));
    }

    public static Tag[] getQuoteValues() {
        return getValues(t -> t.name().startsWith("QUOTE"));
    }

    public static Tag[] getTableValues() {
        return getValues(t -> t.name().startsWith("TABLE"));
    }

    public static Tag[] getCodeValues() {
        return getValues(t -> t.name().startsWith("CODE"));
    }

    public static Tag[] getTextValues() {
        List<Tag> tags = new ArrayList<>(Arrays.asList(values()));
        for (Tag t : getCommentValues()) tags.remove(t);
        for (Tag t : getQuoteValues()) tags.remove(t);
        for (Tag t : getTableValues()) tags.remove(t);
        for (Tag t : getCodeValues()) tags.remove(t);
        for (Tag t : getHeaderValues()) tags.remove(t);
        return tags.toArray(new Tag[0]);
    }

    public static Tag[] getHeaderValues() {
        return getValues(t -> t.name().startsWith("HEADER"));
    }

    private static Tag[] getValues(Predicate<Tag> filter) {
        return Arrays.stream(values()).filter(filter).toArray(Tag[]::new);
    }
}