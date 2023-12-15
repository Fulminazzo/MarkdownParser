package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.TextType;
import it.fulminazzo.markdownparser.utils.Constants;

import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeaderNode extends TextBlock {
    protected int headerSize;
    protected TextNode headerText;

    public HeaderNode(String text) {
        super(text);
    }

    @Override
    public void setText(String text, TextType textType, boolean checkHeader) {
        if (text == null) return;
        while (text.startsWith("\n")) text = text.substring(1);
        String[] tmp = text.split("\n");
        String headerText = tmp[0];
        Matcher matcher = Pattern.compile(Constants.HEADER_REGEX).matcher(headerText);
        if (matcher.matches()) {
            this.headerSize = (int) headerText.split(" ")[0].chars().filter(c -> c == '#').count();
            this.headerText = new TextNode(matcher.group(1), TextType.NORMAL, false);
        } else {
            this.headerSize = 0;
            this.headerText = null;
        }
        text = text.substring(headerText.length());
        if (text.endsWith("\n") == false) text += "\n";
        matcher = Pattern.compile(Constants.CODE_REGEX_MULTIPLE_LINES).matcher(text);
        while (matcher.find())
            text = text.replace(matcher.group(1), String.format("<CODE>%s</CODE>",
                    Base64.getEncoder().encodeToString(matcher.group(1).getBytes())));
        tmp = text
                //.replaceAll(Constants.CODE_REGEX_MULTIPLE_LINES, "<CODE>")
                .split(Constants.HEADER_REGEX.replace("#{1,6}", "#".repeat(headerSize)));
        System.out.println(String.format("TEXT: {%s}", text));
        System.out.println(String.format("REPLACEMENT: {%s}", text.replaceAll(Constants.CODE_REGEX_MULTIPLE_LINES, "<CODE>")));
        matcher = Pattern.compile(Constants.CODE_REGEX_MULTIPLE_LINES).matcher(tmp[0]);
        while (matcher.find()) {
            String group = matcher.group(1);
            Matcher innerMatcher = Pattern.compile("<CODE>(.*)</CODE>").matcher(group);
            if (innerMatcher.find())
                tmp[0] = tmp[0].replace(matcher.group(1),
                        new String(Base64.getDecoder().decode(innerMatcher.group(1))));
        }
        System.out.println(String.format("TMP0: {%s}", tmp[0]));
        super.setText(tmp[0], textType, checkHeader);
        if (tmp.length > 1)
            addNode(new HeaderNode(text.substring(text.indexOf(tmp[0]) + tmp[0].length())));
        while (nextNode != null) {
            if (nextNode instanceof HeaderNode) if (((HeaderNode) nextNode).headerSize == headerSize) return;
            Node tmpNode = nextNode;
            removeNode(tmpNode);
            tmpNode.nextNode = null;
            addChildNode(tmpNode);
        }
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && (headerText == null || headerText.isEmpty());
    }

    @Override
    public String serialize() {
        String serialize = super.serialize();
        if (serialize == null) serialize = "";
        if (headerText != null) serialize = String.format("%s %s\n%s", "#".repeat(headerSize), headerText.serialize(), serialize);
        return serialize;
    }

    @Override
    public String getContent() {
        String output = "{" + Constants.SEPARATOR + "\n";
        output += Constants.SEPARATOR + String.format("header-size: %s\n", headerSize);
        output += Constants.SEPARATOR + String.format("header-text: %s\n}", (headerText == null ? "null" : headerText.toString())
                .replace("\n", "\n" + Constants.SEPARATOR.repeat(2)));
        return output;
    }
}
