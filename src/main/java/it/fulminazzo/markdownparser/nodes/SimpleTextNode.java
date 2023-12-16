package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.utils.Constants;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class SimpleTextNode extends Node {
    protected String text;

    public SimpleTextNode() {
        this(null);
    }

    public SimpleTextNode(String text) {
        setText(text);
    }

    public void setText(String text) {
        if (text == null) return;
        this.text = text.replace("\n", " ");
        //TODO: Links
        if (true) return;
        String[] tmp = text.split(Constants.LINK_REGEX);
        Matcher matcher = Pattern.compile(Constants.LINK_REGEX).matcher(text);
        if (tmp.length > 0) {
            this.text = tmp[0];
            for (int i = 1; i < tmp.length; i++) {
                String t = tmp[i];
                //TODO: Links
                //if (matcher.find()) addNode(new LinkNode(matcher.group()));
                if (!t.isEmpty()) addNode(new SimpleTextNode(t));
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return text == null || text.isEmpty();
    }

    @Override
    public String serialize() {
        return text;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", getClass().getSimpleName(), serialize());
    }
}
