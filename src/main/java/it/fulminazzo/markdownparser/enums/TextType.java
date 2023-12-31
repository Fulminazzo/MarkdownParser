package it.fulminazzo.markdownparser.enums;

import it.fulminazzo.markdownparser.utils.Constants;
import lombok.Getter;

import java.util.Arrays;

/**
 * An enum containing every text
 * format allowed by Markdown.
 */
@Getter
public enum TextType {
    STRONG("**", Constants.STRONG_REGEX),
    STRONG2("__", Constants.STRONG_REGEX_2),
    ITALIC("_", Constants.ITALIC_REGEX),
    ITALIC2("*", Constants.ITALIC_REGEX_2),
    STRIKETHROUGH("~~", Constants.STRIKETHROUGH),
    NORMAL("", null);

    private final String idChar;
    private final String regex;

    TextType(String idChar, String regex) {
        this.idChar = idChar;
        this.regex = regex;
    }

    /**
     * Check if the given text matches the text type format.
     *
     * @param text the text
     * @return the boolean
     */
    public boolean matches(String text) {
        if (text == null) return false;
        text = text.trim();
        return text.startsWith(idChar) && text.endsWith(idChar);
    }

    /**
     * Gets the content inside the text type format.
     *
     * @param text the text
     * @return the content
     */
    public String getContent(String text) {
        if (!matches(text)) return text;
        text = text.trim();
        if (text.length() < idChar.length() * 2) return text;
        text = text.substring(idChar.length());
        text = text.substring(0, text.length() - idChar.length());
        return text;
    }

    /**
     * Returns the text type corresponding to the idChar.
     *
     * @param idChar the id char
     * @return the text type
     */
    public static TextType fromChar(String idChar) {
        if (idChar == null) return null;
        return Arrays.stream(TextType.values()).filter(c -> c.idChar.equalsIgnoreCase(idChar)).findFirst().orElse(null);
    }

    public boolean equals(TextType textType) {
        if (textType == null) return false;
        return toString().equals(textType.toString());
    }

    @Override
    public String toString() {
        String name = name();
        if (name.contains("_")) name = name.substring(0, name.indexOf("_"));
        return name;
    }
}
