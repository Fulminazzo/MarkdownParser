package it.fulminazzo.markdownparser.objects;

import it.fulminazzo.markdownparser.nodes.Node;
import it.fulminazzo.markdownparser.utils.Constants;
import it.fulminazzo.markdownparser.utils.NodeUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TableRow {
    private final List<Node> contents;

    public TableRow(String rawContents) {
        this.contents = new ArrayList<>();
        setContents(rawContents);
    }

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

    public List<String> getStringContents() {
        return contents.stream().map(Node::serialize).collect(Collectors.toList());
    }

    public String getColumnContent(int column) {
        if (column < 0 || column > getColumnSize()) throw new ArrayIndexOutOfBoundsException(getColumnSize());
        return contents.get(column).serialize();
    }

    public int getColumnSize() {
        return contents.size();
    }

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