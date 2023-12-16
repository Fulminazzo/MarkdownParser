package it.fulminazzo.markdownparser;

import it.fulminazzo.markdownparser.enums.Tag;
import it.fulminazzo.markdownparser.nodes_prev.Node;
import it.fulminazzo.markdownparser.nodes_prev.RootNode;
import it.fulminazzo.markdownparser.utils.NodeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownParser {

    public static void main(String[] args) throws IOException {
        if (true) {
            InputStream file = MarkdownParser.class.getResourceAsStream("/test.md");
            String content = new String(file.readAllBytes());
            file.close();;

            it.fulminazzo.markdownparser.nodes.RootNode rootNode = new it.fulminazzo.markdownparser.nodes.RootNode(content);
            File file1 = new File("test.md");
            if (!file1.exists()) file1.createNewFile();
            FileOutputStream fileOutputStream =new FileOutputStream(file1);
            fileOutputStream.write(rootNode.serialize().getBytes());
            fileOutputStream.close();
            /*it.fulminazzo.markdownparser.nodes.Node node = NodeUtils.parseRaw(content);
            it.fulminazzo.markdownparser.nodes.Node tmp = node;
            System.out.println("PRINTING NODE");
            while (node != null) {
                System.out.println(node);
                node = node.getNext();
            }
            node = tmp;
            while (node != null) {
                System.out.println(node.serialize());
                node = node.getNext();
            }*/
            return;
        }

        if (false) {
            String text = "# Hello world\n" +
                    "This is my text\n" +
                    "\n" +
                    "This is another great text\n" +
                    "\n" +
                    "## Title\n" +
                    "This is another title of the great text\n" +
                    "\n" +
                    "# Hello friend\n" +
                    "Can you see me?\n" +
                    "You should not\n" +
                    "\n" +
                    "## CAN YOU?!";
            //Matcher matcher = Pattern.compile(Constants.HEADER_REGEX).matcher(text);
            return;
        }
        InputStream file = MarkdownParser.class.getResourceAsStream("/test1.md");
        String content = new String(file.readAllBytes());
        file.close();

        if (false) {
            String text = "\"Questo `dovrebbe` essere presto, questo``` no, ```uesto si```\"\n" +
                    "Questo `pure`\n" +
                    "\n" +
                    "Voglio ```java\n" +
                    "il cazzo\n" +
                    "```\n" +
                    "Voglio ```questo``` ```gg.\n" +
                    "```java\n" +
                    "fhjkdsalfhjksdhfjkldshjfkldsdshjkfsdhla\n" +
                    "fdisfdisajlfkdsfdlsahk\n" +
                    "```\n" +
                    "\n" +
                    "\n" +
                    "Voglio quello che c'e' tra le ```\n" +
                    "amico,\n" +
                    "`e qui`che`succede\n" +
                    "\n" +
                    "test ok uhifhjdskafhsdjkfhjksd ```anothercode in what ```langauge```\n" +
                    "```java\n" +
                    "public static volatile public static void main(String[] args) {\n" +
                    "    System.out.println(\"Simple java code\");\n" +
                    "}\n" +
                    "``` \n";
            String regex = "\n *```([\n-~ ]*)``` *\n";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = null;
            content = text;
            do {
                matcher = pattern.matcher(content);
                if (matcher.find()) {
                    String prev = content.split(regex)[0];
                    String c = matcher.group(1).split("``` *\n")[0];
                    //System.out.println(String.format("PREV:\n%s\n:PREV", prev));
                    //System.out.println(String.format("C:\n%s\n:C", c));
                    text = text.replace("```" + c + "```", "<code>" + c + "<code>");
                    content = content.substring(prev.length());
                    content = content.substring(c.length() + "```".length() * 2);
                    //System.out.println(String.format("CONTENT:\n%s\n:CONTENT", content));
                } else matcher = null;
            } while (matcher != null);
            return;
        }

        if (false)content =
                "| `Simple2` Table |\n" +
                "| - |\n" +
                "| Working table|\n" +
                "| Workin2 table|\n" +
                "| Workin3 table|\n" +
                "| Workin4 table|\n" +
                "\n";
        /*content = "\"Questo `dovrebbe` essere presto, questo``` no, ```uesto si```\"\n" +
                "Questo `pure`\n" +
                "\n" +
                "Voglio ```java\n" +
                "il cazzo\n" +
                "```\n" +
                "Voglio ```questo``` ```gg.\n" +
                "```java\n" +
                "fhjkdsalfhjksdhfjkldshjfkldsdshjkfsdhla\n" +
                "fdisfdisajlfkdsfdlsahk\n" +
                "```\n" +
                "\n" +
                "\n" +
                "Voglio quello che c'e' tra le ```\n" +
                "amico,\n" +
                "`e qui`che`succede\n" +
                "\n" +
                "test ok uhifhjdskafhsdjkfhjksd ```anothercode in what ```langauge```\n" +
                "```java\n" +
                "public static volatile public static void main(String[] args) {\n" +
                "    System.out.println(\"Simple java code\");\n" +
                "}\n" +
                "``` \n" +
                "This is a perfect text **amazing\n" +
                "right?**";

        content = "Voglio ```java\n" +
                "il cazzo\n" +
                "```\n" +
                "Voglio ```questo``` ```gg.\n" +
                "```java\n" +
                "fhjkdsalfhjksdhfjkldshjfkldsdshjkfsdhla\n" +
                "fdisfdisajlfkdsfdlsahk\n" +
                "```\n" +
                "\n" +
                "\n" +
                "Voglio quello che c'e' tra le ```";*/
        /*content = "```java\n" +
                "public static volatile public static void main(String[] args) {\n" +
                "    System.out.println(\"Simple java code\");\n" +
                "}\n" +
                "``` ";*/
        /*content = "# Hello world\n" +
                "This is my text\n" +
                "\n" +
                "```yaml\n" +
                "\n" +
                "\n" +
                "# This is a beautiful yaml comment\n" +
                "```\n\n" *//*+
                "\n" +
                "# What\n" +
                "The fuck";*//*
        ;*/
        content = "\n" +
                "|Table|\n" +
                "                       | - |\n" +
                "| Outstanding table | \n" +
                "ok rip nice\n" +
                "\n" +
                "| `Simple2` Table |\n" +
                "| - |\n" +
                "| Working table|\n" +
                "| Workin2 table|\n" +
                "| Workin3 table|\n" +
                "| Workin4 table|";

        Node node = new RootNode(content);
        System.out.println(node);
        System.out.println(node.serialize());
        File file2 = new File("test.md");
        FileOutputStream fileOutputStream = new FileOutputStream(file2);
        fileOutputStream.write(node.serialize().getBytes());
        fileOutputStream.close();
    }

}