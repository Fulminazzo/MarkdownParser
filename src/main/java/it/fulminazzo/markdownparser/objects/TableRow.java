package it.fulminazzo.markdownparser.objects;

import it.fulminazzo.markdownparser.nodes.Node;
import it.fulminazzo.markdownparser.utils.Constants;
import it.fulminazzo.markdownparser.utils.NodeUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the row of a table.
 */
@Getter
public class TableRow {
    private final List<Node> contents;

    /**
     * Instantiates a new Table row.
     *
     * @param rawContents the raw contents
     */
    public TableRow(String rawContents) {
        this.contents = new ArrayList<>();
        setContents(rawContents);
    }

    /**
     * Sets contents from a raw string.
     *
     * @param rawContents the raw contents
     */
    public void setContents(String rawContents) {
        this.contents.clear();
        if (rawContents == null) return;
        rawContents = rawContents.trim();
        if (!rawContents.startsWith("|")) return;
        rawContents = rawContents.substring(1);
        this.contents.addAll(Arrays.stream(rawContents.split("\\|"))
                .map(String::trim)
                .map(NodeUtils::formatRawText)
                .collect(Collectors.toList()));
    }

    /**
     * Gets contents as a string list.
     *
     * @return the contents
     */
    public List<String> getStringContents() {
        return contents.stream().map(Node::serialize).collect(Collectors.toList());
    }

    /**
     * Gets the corresponding column content.
     *
     * @param column the column number
     * @return the column content
     */
    public String getColumnContent(int column) {
        if (column < 0 || column > getColumnSize()) throw new ArrayIndexOutOfBoundsException(getColumnSize());
        return contents.get(column).serialize();
    }

    /**
     * Gets column size.
     *
     * @return the column size
     */
    public int getColumnSize() {
        return contents.size();
    }

    /**
     * Converts the contents into a Markdown format as string.
     *
     * @return the string
     */
    public String serialize() {
        String serialize = "| ";
        serialize += contents == null ? "" : String.join(" | ", getStringContents());
        if (serialize.equals("| ")) serialize += "|";
        serialize = serialize.replace("\n", " ");
        return serialize;
    }

    @Override
    public String toString() {
        String output = String.format("%s {\n", getClass().getSimpleName());
        for (int i = 0; i < contents.size(); i++) {
            output += Constants.SEPARATOR + contents.get(i)
                    .toString().replace("\n", "\n" + Constants.SEPARATOR);
            if (i < contents.size() - 1) output += ",";
            output += "\n";
        }
        return output + "}";
    }
}