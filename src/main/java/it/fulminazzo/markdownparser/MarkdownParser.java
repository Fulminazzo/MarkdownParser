package it.fulminazzo.markdownparser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class MarkdownParser {

    public static void main(String[] args) throws IOException {
        int tries = 3;
        Date start = new Date();
        for (int i = 0; i < tries; i++) {
            Date date = new Date();
            InputStream file = MarkdownParser.class.getResourceAsStream("/test1.md");
            String content = new String(file.readAllBytes());
            file.close();

            it.fulminazzo.markdownparser.nodes.RootNode rootNode = new it.fulminazzo.markdownparser.nodes.RootNode(content);
            //System.out.println(rootNode.serialize());
            File file1 = new File("test.md");
            if (!file1.exists()) file1.createNewFile();
            FileOutputStream fileOutputStream =new FileOutputStream(file1);
            fileOutputStream.write(rootNode.serialize().getBytes());
            fileOutputStream.close();
            Date finalDate = new Date();
            System.out.println(String.format("Test time: %ss", ((double) finalDate.getTime() - date.getTime()) / 1000));
        }
        Date finalDate = new Date();
        System.out.println(String.format("Overall time: %ss", ((double) finalDate.getTime() - start.getTime()) / 1000));
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

}