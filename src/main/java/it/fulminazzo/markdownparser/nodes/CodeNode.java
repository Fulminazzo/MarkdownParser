package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.Tag;
import it.fulminazzo.markdownparser.objects.ContentMap;
import it.fulminazzo.markdownparser.utils.Constants;
import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Node that represents a code block.
 */
@Getter
public class CodeNode extends TagNode {
    private String language;
    private String code;

    /**
     * Instantiates a new Code node.
     */
    public CodeNode() {
        this(null);
    }

    /**
     * Instantiates a new Code node.
     *
     * @param rawContent the raw content
     */
    public CodeNode(String rawContent) {
        super(rawContent, Tag.getCodeValues());
    }

    /**
     * Instantiates a new Code node.
     *
     * @param language the language
     * @param code     the code
     */
    public CodeNode(String language, String code) {
        super(Tag.getCodeValues());
        setContent(language, code);
    }

    @Override
    protected void setContents(String rawContent) {
        for (Tag tag : Tag.getCodeValues()) {
            Matcher matcher = Pattern.compile(tag.getRegex()).matcher(rawContent);
            if (matcher.find()) {
                rawContent = matcher.group(2);
                if (tag.equals(Tag.CODE2)) rawContent = matcher.group(1) + "\n" + rawContent;
                break;
            }
        }
        String[] tmp = rawContent.split("[ \n]");
        String language = tmp.length == 0 ? "" : tmp[0];
        if (rawContent.contains("\n") && language.matches("^[a-zA-Z]*$") && tmp.length > 1) {
            setContent(language, rawContent.substring(language.length()));
        } else {
            this.language = null;
            this.code = rawContent;
        }
    }

    /**
     * Sets content.
     *
     * @param language the language
     * @param code     the code
     */
    public void setContent(String language, String code) {
        if (language != null && language.replace("\n", "").trim().isEmpty())
            language = null;
        this.language = language;
        this.code = code;
    }

    @Override
    public String serialize() {
        if (code == null) return "";
        String codeSeparator = code.contains("\n") || language != null ? Constants.CODE_SEPARATOR : Constants.CODE_SEPARATOR_2;
        String serialize = codeSeparator;
        serialize += language == null ? "" : (language);
        serialize += code;
        if (code.contains("\n") && serialize.endsWith("\n\n")) serialize = serialize.substring(0, serialize.length() - "\n".length());
        serialize += codeSeparator;
        if (code.contains("\n")) serialize += "\n";
        return serialize;
    }

    @Override
    protected ContentMap getContentMap() {
        return super.getContentMap().set("language", language, "code", code);
    }
}
