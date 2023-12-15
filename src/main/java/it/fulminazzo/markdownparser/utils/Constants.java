package it.fulminazzo.markdownparser.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constants {
    public static final String SEPARATOR = "    ";
    public static final String TEXT_SEPARATOR = "\n\n";
    public static final String CODE_SEPARATOR = "```";
    public static final String CODE_SEPARATOR_2 = "`";
    public static final String CODE_REGEX_SINGLE = "([^`]|^)`((?:[^`\n])+)`(?:[^`]|$)";
    public static final String CODE_REGEX_MULTIPLE = "([^`]|^)```((?:[^`\n])+)```(?:[^`]|$)";
    public static final String CODE_REGEX_MULTIPLE_LINES = "(?:^|\n) *```([\n-~ ]*)``` *\n";
    public static final String COMMENT_1_OPENING = "<!--";
    public static final String COMMENT_1_CLOSING = "-->";
    public static final String COMMENT_2_OPENING = "[//]: # (";
    public static final String COMMENT_2_CLOSING = ")\n";
    public static final String LINK_REGEX = "\\[([^\\]]*)\\]\\(([^\\)]*)\\)";
    public static final String HEADER_REGEX = "(?:\n *|^)#{1,6} ([^\n]+)";
    private static final String TABLE_REGEX =
            "((?: *\\| *(?:[^|\\n]+) *){1}\\|) *\\n" +
            "((?: *\\| *-+ *){1}\\|) *\\n" +
            "((?:(?:(?: *\\| *(?:[^|\\n]+) *){1}\\|) *\\n)*)";
    public static final int MAX_TABLE_LENGTH = 5;

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

    public static String[] splitFirstRegex(String string, String regex) {
        if (string == null || regex == null) return null;
        String[] tmp = string.split(regex);
        if (tmp.length > 1) {
            String[] result = new String[2];
            result[1] = "";
            Matcher matcher = Pattern.compile(regex).matcher(string);
            result[0] = (matcher.find() ? matcher.group(1) : "");
            for (int i = 1; i < tmp.length; i++) {
                String t = tmp[i];
                if (!t.isEmpty()) result[1] = result[1] + t;
                if (matcher.find()) {
                    String group = matcher.group();
                    if (group != null) result[1] = result[1] + group;
                }
            }
            return result;
        } else return null;
    }

    public static String[] splitRegex(String string, String regex) {
        if (string == null || regex == null) return null;
        String[] tmp = string.split(regex);
        if (tmp.length > 1) {
            List<String> result = new ArrayList<>();
            Matcher matcher = Pattern.compile(regex).matcher(string);
            for (int i = 1; i < tmp.length; i++) {
                String t = tmp[i];
                if (matcher.find()) {
                    String group = matcher.group();
                    if (group != null) t = group + t;
                }
                result.add(t);
            }
            return result.toArray(new String[0]);
        } else return new String[]{string};
    }
}