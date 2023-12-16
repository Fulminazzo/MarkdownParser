package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.utils.NodeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RootNode extends Node {

    public RootNode(File file) throws IOException {
        this(new FileInputStream(file));
    }

    public RootNode(InputStream inputStream) throws IOException {
        this(new String(inputStream.readAllBytes()));
        inputStream.close();
    }

    public RootNode(String rawText) {
        child = NodeUtils.formatRawText(rawText);
    }

    @Override
    public String serialize() {
        return serializeChildren();
    }
}
