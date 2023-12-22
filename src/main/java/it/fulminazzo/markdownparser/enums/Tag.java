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

/**
 * An enum that represents any tag supported by the program.
 * Each tag uses regular expressions to convert any valid string
 * in a &#60;TAG&#62;(Base64)&#60;/TAG&#62; format.
 */
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

    /**
     * Gets tags regex.
     *
     * @return the tags regex
     */
    public String getTagsRegex() {
        return ".*<" + tagName + ">(.*)</" + tagName + ">.*";
    }

    /**
     * Remove the tags from a string and decodes it from Base64.
     *
     * @param rawText the raw text
     * @return the string
     */
    public String unParse(String rawText) {
        if (rawText == null) return null;
        Matcher matcher = Pattern.compile(rawText).matcher(getTagsRegex());
        if (matcher.find()) rawText = matcher.group(1);
        return Base64Utils.decode(rawText);
    }

    /**
     * Gets a string wrapped in &#60;TAG&#62;&#60;/TAG&#62;
     *
     * @param string the string
     * @return the tagged string
     */
    public String getTaggedString(String string) {
        if (string == null) return null;
        return getTagsRegex().replace("(.*)", string).replace(".*", "");
    }

    /**
     * Create a node from the given text.
     * If check is true, checks that the string
     * matches a valid tag.
     *
     * @param rawText the raw text
     * @param check   the check
     * @return the node
     */
    public Node create(String rawText, boolean check) {
        if (rawText == null || creator == null) return null;
        if (check && !Pattern.compile(getTagsRegex()).matcher(rawText).find()) rawText = getTaggedString(rawText);
        return creator.apply(rawText);
    }

    /**
     * Converts a string into the &#60;TAG&#62;(Base64)&#60;/TAG&#62; format
     * for every tag.
     *
     * @param text the text
     * @return the string
     */
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

    /**
     * Check if the text has a valid tag.
     *
     * @param text the text
     * @return the boolean
     */
    public static boolean hasValidTag(String text) {
        if (text == null) return false;
        Matcher matcher = Pattern.compile(Constants.TAGS_FINDER_REGEX).matcher(text);
        if (matcher.find())
            try {
                Tag.valueOf(matcher.group(2));
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        return false;
    }

    /**
     * Get comment tags.
     *
     * @return the tags
     */
    public static Tag[] getCommentValues() {
        return getValues(t -> t.name().startsWith("COMMENT"));
    }

    /**
     * Get quote tags.
     *
     * @return the tags
     */
    public static Tag[] getQuoteValues() {
        return getValues(t -> t.name().startsWith("QUOTE"));
    }

    /**
     * Get table tags.
     *
     * @return the tags
     */
    public static Tag[] getTableValues() {
        return getValues(t -> t.name().startsWith("TABLE"));
    }

    /**
     * Get code tags.
     *
     * @return the tags
     */
    public static Tag[] getCodeValues() {
        return getValues(t -> t.name().startsWith("CODE"));
    }

    /**
     * Get text tags.
     *
     * @return the tags
     */
    public static Tag[] getTextValues() {
        List<Tag> tags = new ArrayList<>(Arrays.asList(values()));
        for (Tag t : getCommentValues()) tags.remove(t);
        for (Tag t : getQuoteValues()) tags.remove(t);
        for (Tag t : getTableValues()) tags.remove(t);
        for (Tag t : getCodeValues()) tags.remove(t);
        for (Tag t : getHeaderValues()) tags.remove(t);
        return tags.toArray(new Tag[0]);
    }

    /**
     * Get header tags.
     *
     * @return the tags
     */
    public static Tag[] getHeaderValues() {
        return getValues(t -> t.name().startsWith("HEADER"));
    }

    /**
     * Get tags using the specified filter.
     *
     * @param filter the filter
     * @return the tags
     */
    private static Tag[] getValues(Predicate<Tag> filter) {
        return Arrays.stream(values()).filter(filter).toArray(Tag[]::new);
    }
}