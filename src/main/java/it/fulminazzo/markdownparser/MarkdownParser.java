package it.fulminazzo.markdownparser;

import it.fulminazzo.markdownparser.nodes.Node;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

public class MarkdownParser {

    public static String getTime() {
        return new SimpleDateFormat("HH-mm-ss.SSS").format(new Date());
    }

    public static <T> T executeTask(String first, String second, Callable<T> runnable, long minTime) {
        Date date = new Date();
        String firstMessage = String.format("[%s] %s", getTime(), first);
        T t = null;
        try {
            t = runnable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (new Date().getTime() - date.getTime() < minTime) return t;
        String secondMessage = String.format("[%s] %s", getTime(), second);
        System.out.println(firstMessage);
        System.out.println(secondMessage);
        return t;
    }

    public static void main(String[] args) throws IOException {
        if (false) {
            InputStream file = MarkdownParser.class.getResourceAsStream("/test3.md");
            String content = new String(file.readAllBytes());
            file.close();

            Node rootNode = new it.fulminazzo.markdownparser.nodes.RootNode(content);
            //rootNode = rootNode.getChild().getLastNode();
            System.out.println(rootNode);
            System.out.println(rootNode.serialize());
            return;
        }
        int tries = 1;
        Date start = new Date();
        for (int i = 0; i < tries; i++) {
            Date date = new Date();
            InputStream file = MarkdownParser.class.getResourceAsStream("/test.md");
            String content = new String(file.readAllBytes());
            file.close();

            it.fulminazzo.markdownparser.nodes.RootNode rootNode = new it.fulminazzo.markdownparser.nodes.RootNode(content);
            File file1 = new File("test.md");
            if (!file1.exists()) file1.createNewFile();
            FileOutputStream fileOutputStream =new FileOutputStream(file1);
            fileOutputStream.write(rootNode.serialize().getBytes());
            fileOutputStream.close();
            Date finalDate = new Date();
            System.out.println(String.format("Test [%s] time: %ss", i + 1, ((double) finalDate.getTime() - date.getTime()) / 1000));
        }
        Date finalDate = new Date();
        System.out.println(String.format("Overall time: %ss", ((double) finalDate.getTime() - start.getTime()) / 1000));
    }

}