package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.utils.Constants;
import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class LinkNode extends SimpleTextNode {
    private final String link;

    public LinkNode(String text) {
        super("");
        Pattern pattern = Pattern.compile(Constants.LINK_REGEX);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            setText(matcher.group(1));
            this.link = matcher.group(2).split(" ")[0];
        } else {
            setText(text);
            this.link = null;
        }
    }

    @Override
    public String serialize() {
        return String.format("[%s](%s)", text, link);
    }

    @Override
    public String getContent() {
        String output = "{" + Constants.SEPARATOR + "\n";
        if (text != null) output += Constants.SEPARATOR + String.format("text: %s\n", text);
        if (link != null) output += Constants.SEPARATOR + String.format("link: %s\n", link);
        output += "\n}";
        return output;
    }
}
