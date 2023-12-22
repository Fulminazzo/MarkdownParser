package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.utils.NodeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A wrapper Node that can be used to parse Markdown from
 * files, input streams and strings.
 */
public class RootNode extends Node {

    /**
     * Instantiates a new Root node.
     *
     * @param file the file
     * @throws IOException the io exception
     */
    public RootNode(File file) throws IOException {
        this(new FileInputStream(file));
    }

    /**
     * Instantiates a new Root node.
     *
     * @param inputStream the input stream
     * @throws IOException the io exception
     */
    public RootNode(InputStream inputStream) throws IOException {
        this(new String(inputStream.readAllBytes()));
        inputStream.close();
    }

    /**
     * Instantiates a new Root node.
     *
     * @param rawContent the raw content
     */
    public RootNode(String rawContent) {
        setContent(rawContent);
    }

    @Override
    public void setContent(String rawContent) {
        child = NodeUtils.formatRawText(rawContent);
    }

    @Override
    public String serialize() {
        String serialize = serializeChildren();
        if (serialize == null) return "";
        serialize = serialize.trim();
        while (serialize.startsWith("\n")) serialize = serialize.substring(1);
        return serialize;
    }
}
