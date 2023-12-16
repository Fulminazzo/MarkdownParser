package it.fulminazzo.markdownparser.nodes_prev;

import it.fulminazzo.markdownparser.utils.Constants;
import lombok.Getter;

@Getter
public class CodeBlock extends Node {
    private final String language;
    private final String code;

    public CodeBlock(String rawText) {
        for (String codeSeparator : Constants.getCodeSeparators()) {
            if (rawText.startsWith(codeSeparator)) rawText = rawText.substring(codeSeparator.length());
            if (rawText.endsWith(codeSeparator))
                rawText = rawText.substring(0, rawText.length() - codeSeparator.length());
        }
        String[] tmp = rawText.split("[ \n]");
        String language = tmp.length == 0 ? "" : tmp[0];
        if (rawText.contains("\n") && language.matches("^[a-zA-Z]*$") && tmp.length > 1) {
            this.language = language;
            this.code = rawText.substring(language.length());
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
        String codeSeparator = code.contains("\n") ? "\n" + Constants.CODE_SEPARATOR : Constants.CODE_SEPARATOR_2;
        return String.format("%s%s%s%s", codeSeparator, language == null ? "" : (language + " "), code, codeSeparator);
    }

    @Override
    public String getContent() {
        String output = "{" + "\n";
        if (language != null) output += Constants.SEPARATOR + String.format("language: %s\n", language);
        output += Constants.SEPARATOR + String.format("code: %s\n}", code.replace("\n", "\n" + Constants.SEPARATOR.repeat(2)));
        return output;
    }
}
