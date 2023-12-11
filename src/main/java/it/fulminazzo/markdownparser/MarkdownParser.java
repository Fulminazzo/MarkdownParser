package it.fulminazzo.markdownparser;

import it.fulminazzo.markdownparser.enums.TextType;
import it.fulminazzo.markdownparser.nodes.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownParser {

    public static void main(String[] args) throws IOException {
        Node rootNode = new RootNode(MarkdownParser.class.getResourceAsStream("/test1.md"));
        //String text = "lol****great **test** ~~ friend ~~ <!--comment lol-->\n";
        //RootNode rootNode = new RootNode(text);
        //text = "Ciao ~~amico~~ mio";
        //TextNode rootNode = new TextNode(text);
        //rootNode = ((ContainerNode) rootNode).getChildNode();
        //rootNode = rootNode.getNextNode().getNextNode();
        //rootNode = ((ContainerNode) rootNode).getChildNode().getNextNode();
        System.out.println(rootNode);
        System.out.println(rootNode.serialize());
        /*"g".split()
        if (matcher.matches()) {
            System.out.println(matcher.group(1));
        }*/
        //System.out.println(rootNode.print());
        /*String text = "This is a ****test.\n" +
                "This text ~~looks~~ cool.\n" +
                "What **about this?\n" +
                "<!-- this is a comment rip-->\n" +
                "\n" +
                "[//]: # (lol)\n" +
                "<!-- this is anther comment rip-->";
        Pattern pattern = Pattern.compile("<!--(.*)-->(.*)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            System.out.println(matcher.groupCount());
            System.out.println(matcher.group(1));
        } else System.out.println("Not match");*/
    }

    public static boolean textRegex(String string, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        if (matcher.matches()) return matcher.groupCount() > 1;
        return false;
    }
}