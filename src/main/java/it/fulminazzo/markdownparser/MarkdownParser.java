package it.fulminazzo.markdownparser;

import it.fulminazzo.markdownparser.enums.TextType;
import it.fulminazzo.markdownparser.nodes.*;

import javax.management.modelmbean.ModelMBeanConstructorInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownParser {

    public static void main(String[] args) throws IOException {
        Node rootNode = new RootNode(MarkdownParser.class.getResourceAsStream("/test1.md"));
        System.out.println(rootNode);
        System.out.println(rootNode.serialize());
    }

}