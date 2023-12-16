package it.fulminazzo.markdownparser.nodes_prev;

import it.fulminazzo.markdownparser.utils.Constants;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class SimpleTextNode extends Node {
    protected String text;

    public SimpleTextNode(String text) {
        if (text == null) return;
        this.text = text.replace("\n", " ");
        String[] tmp = text.split(Constants.LINK_REGEX);
        Matcher matcher = Pattern.compile(Constants.LINK_REGEX).matcher(text);
        if (tmp.length > 0) {
            this.text = tmp[0];
            for (int i = 1; i < tmp.length; i++) {
                String t = tmp[i];
                if (matcher.find()) addNode(new LinkNode(matcher.group()));
                if (!t.isEmpty()) addNode(new SimpleTextNode(t));
            }
        }
    }

    @Override
    public String serialize() {
        return text;
    }

    @Override
    public String getContent() {
        return text;
    }
}
