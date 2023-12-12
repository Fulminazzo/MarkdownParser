package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.utils.Constants;
import lombok.Getter;

import java.util.Arrays;
import java.util.regex.Pattern;

@Getter
public class CodeBlock extends Node {
    private final String language;
    private final String code;

    public CodeBlock(String rawText) {
        String codeSeparator = Constants.CODE_SEPARATOR;
        if (rawText.startsWith(codeSeparator)) rawText = rawText.substring(codeSeparator.length());
        if (rawText.endsWith(codeSeparator)) rawText = rawText.substring(0, rawText.length() - codeSeparator.length());
        String[] tmp = rawText.split(" ");
        String language = tmp.length == 0 ? "" : tmp[0];
        if (rawText.contains("\n") && language.matches("^[a-zA-Z]*$") && tmp.length > 1) {
            this.language = language;
            this.code = String.join(" ", Arrays.copyOfRange(tmp, 1, tmp.length));
        } else {
            this.language = null;
            this.code = rawText;
        }
    }

    public CodeBlock(String language, String code) {
        this.language = language;
        this.code = code;
    }

    @Override
    public String serialize() {
        String codeSeparator = code.contains("\n") ? Constants.SEPARATOR : Constants.CODE_SEPARATOR_2;
        return String.format("%s%s%s%s", codeSeparator, language == null ? "" : (language + " "), code, codeSeparator);
    }

    @Override
    public String getContent() {
        String output = "{\t\n";
        if (language != null) output += Constants.SEPARATOR + String.format("language: %s\n", language);
        output += Constants.SEPARATOR + String.format("code: %s\n}", code.replace("\n", "\n\t\t"));
        return output;
    }
}
