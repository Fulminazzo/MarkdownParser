package it.fulminazzo.markdownparser.nodes_prev;

import it.fulminazzo.markdownparser.utils.Constants;

public class CommentNode extends SimpleTextNode {

    public CommentNode(String text) {
        super("");
        if (text == null) return;
        for (String[] comment : Constants.getCommentsSeparators()) {
            String cOpening = comment[0];
            String cClosing = comment[1];
            if (text.startsWith(cOpening)) {
                text = text.substring(cOpening.length());
                if (text.endsWith(cClosing))
                    text = text.substring(0, text.length() - cClosing.length());
                setText(text.trim());
            }
        }
    }

    @Override
    public String serialize() {
        return "";
    }
}
