package it.fulminazzo.markdownparser.utils;

import it.fulminazzo.markdownparser.enums.Tag;

import java.util.ArrayList;
import java.util.Base64;
import java.util.DuplicateFormatFlagsException;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constants {
    public static final String SEPARATOR = "    ";
    public static final String TEXT_SEPARATOR = "\n\n";
    // COMMENTS
    public static final String COMMENT_REGEX = "(?:^|\n)\\[\\/\\/\\]: *# \\(([^\n]*)\\)";
    public static final String COMMENT_REGEX_2 = "<!--((?:(?!-->)[\\s\\S])+)-->";
    // QUOTE
    public static final String QUOTE_REGEX = "(?:^|\n) {0,4}(>(?:(?!\n\n)[\\s\\S])+)";
    // TABLE
    private static final String TABLE_REGEX =
            "((?: *\\| *(?:[^|\n]+) *){1}\\|) *\n" +
                    "((?: *\\| *-+ *){1}\\|) *\n" +
                    "((?:(?:(?: *\\| *(?:[^|\n]+) *){1}\\|) *\n)*)";
    // CODE
    public static final String CODE_REGEX_SINGLE = "([^`]|^)`((?:[^`\n])+)`(?:[^`]|$)";
    public static final String CODE_REGEX_MULTIPLE = "([^`]|^)```((?:[^`\n])+)```(?:[^`]|$)";
    public static final String CODE_REGEX_MULTIPLE_LINES = "^```(.+|)\n([\\s\\S]*?)``` *$";
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

    public static final String CODE_SEPARATOR = "```";
    public static final String CODE_SEPARATOR_2 = "`";
    public static final String CODE_REGEX_BASE64 = ".*<CODE>(.*)</CODE>.*";
    public static final String COMMENT_1_OPENING = "<!--";
    public static final String COMMENT_1_CLOSING = "-->";
    public static final String COMMENT_2_OPENING = "[//]: # (";
    public static final String COMMENT_2_CLOSING = ")\n";
    public static final String LINK_REGEX = "\\[([^\\]]*)\\]\\(([^\\)]*)\\)";
    public static final String TABLE_REGEX_BASE64 = ".*<TABLE_N>(.*)</TABLE_N>.*";
    public static final int MAX_TABLE_LENGTH = 5;

    public static String getTagsRegex(Tag tag) {
        return TAGS_REGEX.replace("(?:TAG)", tag.getTagName());
    }

    public static String getHeaderRegex(int num) {
        return HEADER_REGEX.replace("1}", num + "}");
    }

    public static String getTableRegex(int num) {
        return TABLE_REGEX.replace("{1}", "{" + num + "}");
    }

    public static String[][] getCommentsSeparators() {
        return new String[][]{
                new String[]{Constants.COMMENT_1_OPENING, Constants.COMMENT_1_CLOSING},
                new String[]{Constants.COMMENT_2_OPENING, Constants.COMMENT_2_CLOSING},
        };
    }

    public static String[] getCodeSeparators() {
        return new String[]{CODE_SEPARATOR, CODE_SEPARATOR_2};
    }

    public static String compressRawText(String text) {
        text = compressTables(text);
        text = compressCodeBlocks(text);
        return text;
    }

    public static String compressCodeBlocks(String text) {
        return compress(text, Constants.CODE_REGEX_MULTIPLE_LINES, Constants::formatCodeBlock);
    }

    public static String decompressCodeBlocks(String text) {
        return decompress(text, Constants.CODE_REGEX_BASE64, Constants::formatCodeBlock, "CODE");
    }

    public static String compressTables(String text) {
        if (text == null) return null;
        text += "\n";
        for (int i = 1; i < MAX_TABLE_LENGTH; i++) {
            int finalI = i;
            text = compress(text, Constants.TABLE_REGEX.replace("{1}", String.format("{%s}", i)),
                    s -> formatTableBlock(s, finalI));
        }
        if (text.endsWith("\n")) text = text.substring(0, text.length() - 1);
        return text;
    }

    public static String decompressTables(String text) {
        System.out.println(String.format("Decompressing text: \"%s\"", text));
        for (int i = 1; i < MAX_TABLE_LENGTH; i++) {
            int finalI = i;
            text = decompress(text, Constants.TABLE_REGEX_BASE64.replace("_N", "_" + i),
                    s -> formatTableBlock(s, finalI), "TABLE");
        }
        System.out.println(String.format("Decompressed text: \"%s\"", text));
        return text;
    }

    private static String compress(String text, String regex, Function<String, String> formatter) {
        if (text == null) return null;
        Matcher matcher = Pattern.compile(regex).matcher(text);
        while (matcher.find()) {
            String match = matcher.group();
            text = text.replace(match, formatter.apply(Base64Utils.encode(match)));
        }
        return text;
    }

    private static String decompress(String text, String regex, Function<String, String> formatter, String blockName) {
        if (text == null) return null;
        Matcher matcher = Pattern.compile(regex).matcher(text);
        while (matcher.find()) {
            String match = matcher.group(1);
            try {
                text = text.replace(formatter.apply(match), Base64Utils.decode(match));
            } catch (Exception e) {
                text = text.replace(match, match.replace(String.format("<%s>", blockName), String.format("<U%s>", blockName)));
            }
        }
        return text.replace(String.format("<U%s>", blockName), String.format("<%s>", blockName));
    }

    public static String formatCodeBlock(String text) {
        return Constants.CODE_REGEX_BASE64
                .replace("(.*)", text)
                .replace(".*", "");
    }

    public static String formatTableBlock(String text, int n) {
        return Constants.TABLE_REGEX_BASE64
                .replace("_N", "_" + n)
                .replace("(.*)", text)
                .replace(".*", "");
    }
}