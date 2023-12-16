package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.Tag;
import it.fulminazzo.markdownparser.objects.ContentMap;
import it.fulminazzo.markdownparser.utils.Constants;
import lombok.Getter;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class CodeNode extends TagNode {
    private String language;
    private String code;

    public CodeNode() {
        this(null);
    }

    public CodeNode(String rawText) {
        super(rawText, Tag.getCodeValues());
    }

    public CodeNode(String language, String code) {
        super(Tag.getCodeValues());
        setContent(language, code);
    }

    @Override
    protected void setContents(String rawText) {
        for (Tag tag : Tag.getCodeValues()) {
            Matcher matcher = Pattern.compile(tag.getRegex()).matcher(rawText);
            if (matcher.find()) {
                rawText = matcher.group(2);
                if (tag.equals(Tag.CODE)) rawText = matcher.group(1) + "\n" + rawText;
                break;
            }
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

    public void setContent(String language, String code) {
        this.language = language;
        this.code = code;
    }

    @Override
    public String serialize() {
        if (code == null) return "";
        String codeSeparator = code.contains("\n") ? "\n" + Constants.CODE_SEPARATOR : Constants.CODE_SEPARATOR_2;
        String serialize = codeSeparator;
        serialize += language == null ? "" : (language + " ");
        serialize += code;
        if (code.contains("\n") && serialize.endsWith("\n")) serialize = serialize.substring(0, serialize.length() - "\n".length());
        serialize += codeSeparator;
        if (code.contains("\n")) serialize += "\n";
        return serialize;
    }

    @Override
    protected ContentMap getContentMap() {
        return super.getContentMap().set("language", language, "code", code);
    }
}
