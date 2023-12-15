package it.fulminazzo.markdownparser.objects;

import it.fulminazzo.markdownparser.nodes.TextNode;
import it.fulminazzo.markdownparser.utils.Constants;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TableRow {
    private final List<TextNode> contents;

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
                .map(TextNode::new)
                .collect(Collectors.toList()));
    }

    public List<String> getStringContents() {
        return contents.stream().map(TextNode::serialize).collect(Collectors.toList());
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
        return serialize.replace("\n", " ");
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