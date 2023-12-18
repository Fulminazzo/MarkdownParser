package it.fulminazzo.markdownparser.utils;

import it.fulminazzo.markdownparser.enums.Tag;

import java.util.Arrays;

public class Constants {
    public static final String SEPARATOR = "    ";
    public static final String TEXT_SEPARATOR = "\n\n";
    // COMMENTS
    public static final String COMMENT_SINGLE = "[//]: # (%s)";
    public static final String COMMENT_MULTIPLE = "<!-- %s -->";
    public static final String COMMENT_REGEX = "(?:^|\n)\\[\\/\\/\\]: *# \\(([^\n]*)\\)";
    public static final String COMMENT_REGEX_2 = "<!--((?:(?!-->)[\\s\\S])+)-->";
    // QUOTE
    public static final String QUOTE_SINGLE = ">";
    public static final String QUOTE_REGEX = "(?:^|\n) {0,4}(>(?:(?!\n\n)[\\s\\S])+)";
    // TABLE
    private static final String TABLE_REGEX =
            "((?: *\\| *(?:[^|\n]+) *){1}\\|) *\n" +
                    "((?: *\\| *-+ *){1}\\|) *\n" +
                    "((?:(?:(?: *\\| *(?:[^|\n]+) *){1}\\|) *(?:\n|$))*)";
    // CODE
    public static final String CODE_SEPARATOR = "```";
    public static final String CODE_SEPARATOR_2 = "`";
    public static final String CODE_REGEX_SINGLE = "([^`]|^)`((?:[^`\n])+)`(?:[^`]|$)";
    public static final String CODE_REGEX_MULTIPLE = "([^`]|^)```((?:[^`\n])+)```(?:[^`]|$)";
    public static final String CODE_REGEX_MULTIPLE_LINES = "^ *```(.*)\n([\\s\\S]*?)``` *$";
    // LIST
    public static final String LIST_REGEX = "((?:(?:^|\n)(?: ?)- (?:(?:(?!\n- )(?!\\s*\n)[\\S\\s])*)(?:\n|$))+)";
    // LINK
    public static final String LINK_REGEX = "\\[([^\\]]+)\\]\\(([^)\n ]+)(?: \"([^\"]+)\")?\\)";
    // TEXT
    public static final String STRONG_REGEX = "(?:[^*]|^)\\*\\*([^*](?:(?!\\*\\*)(?!\n\n)[\\S\\s])*)\\*\\*(?:[^*]|$)";
    public static final String STRONG_REGEX_2 = "(?:[^_]|^)__([^_](?:(?!__)(?!\n\n)[\\S\\s])*)__(?:[^_]|$)";
    public static final String ITALIC_REGEX = "(?:[^_]|^)_([^_](?:(?!_)(?!\n\n)[\\S\\s])*)_(?:[^_]|$)";
    public static final String ITALIC_REGEX_2 = "(?:[^*]|^)\\*([^*](?:(?!\\*)(?!\n\n)[\\S\\s])*)\\*(?:[^*]|$)";
    public static final String STRIKETHROUGH = "(?:[^~]|^)~~([^~](?:(?!~~)(?!\n\n)[\\S\\s])*)~~(?:[^~]|$)";
    // HEADER
    private static final String HEADER_REGEX = "(?:^|\\n)(#{1}) ([^\n]*)\n((?:(?!\n\\s*#{1,1} )[\\s\\S])*)";
    // TAGS
    private static final String TAGS_REGEX = "((?:(?!<\\/(?:TAG)>)[\\s\\S])*)<(?:TAG)>((?:(?!<\\/(?:TAG)>)[^\n])+)<\\/(?:TAG)>";
    public static final String TAGS_FINDER_REGEX = "((?:(?!<\\/(?:[0-9A-Za-z]+)>)[\\s\\S])*)<([0-9A-Za-z]+)>((?:(?!<\\/(?:[0-9A-Za-z]+)>)[^\n])+)<\\/(?:[0-9A-Za-z]+)>";

    public static int getMaxTableLength() {
        return Arrays.stream(Tag.getTableValues())
                .map(Enum::name)
                .map(s -> s.substring("TABLE".length()))
                .mapToInt(Integer::valueOf)
                .max().orElse(0);
    }

    public static String getTagsRegex(Tag tag) {
        return TAGS_REGEX.replace("(?:TAG)", tag.getTagName());
    }

    public static String getHeaderRegex(int num) {
        return HEADER_REGEX.replace("1}", num + "}");
    }

    public static String getTableRegex(int num) {
        return TABLE_REGEX.replace("{1}", "{" + num + "}");
    }
}