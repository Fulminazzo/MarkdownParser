package it.fulminazzo.markdownparser.nodes_prev;

import java.io.*;

public class RootNode extends ContainerNode {

    public RootNode(File file) throws IOException {
        this(new FileInputStream(file));
    }

    public RootNode(InputStream inputStream) throws IOException {
        this(new String(inputStream.readAllBytes()));
        inputStream.close();
    }

    public RootNode(String rawText) {
        super(rawText);
        checkNodes();
    }

    @Override
    public String getContent() {
        return null;
    }
}