package it.fulminazzo.markdownparser.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegexManager {
    private static final List<Pattern> patterns = new ArrayList<>();

    public static Pattern compilePattern(String regex) {
        return compilePattern(regex, 0);
    }

    public static Pattern compilePattern(String regex, int flags) {
        if (regex == null) return null;
        Pattern pattern = getPattern(regex, flags);
        if (pattern == null) {
            pattern = Pattern.compile(regex, flags);
            patterns.add(pattern);
        }
        return pattern;
    }

    private static Pattern getPattern(String regex, int flags) {
        return regex == null ? null : patterns.stream()
                .filter(p -> p.pattern().equals(regex))
                .filter(p -> p.flags() == flags)
                .findFirst().orElse(null);
    }
}