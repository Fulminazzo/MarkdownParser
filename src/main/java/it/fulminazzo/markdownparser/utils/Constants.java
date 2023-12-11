package it.fulminazzo.markdownparser.utils;

public class Constants {
    public static String SEPARATOR = "    ";
    public static final String TEXT_SEPARATOR = "\n\n";
    public static final String COMMENT_1_OPENING = "<!--";
    public static final String COMMENT_1_CLOSING = "-->";
    public static final String COMMENT_2_OPENING = "[//]: # (";
    public static final String COMMENT_2_CLOSING = ")\n";

    public static String[][] getCommentsSeparators() {
        return new String[][]{
                new String[]{Constants.COMMENT_1_OPENING, Constants.COMMENT_1_CLOSING},
                new String[]{Constants.COMMENT_2_OPENING, Constants.COMMENT_2_CLOSING},
        };
    }
}