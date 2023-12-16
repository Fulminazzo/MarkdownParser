package it.fulminazzo.markdownparser.nodes_prev;

import it.fulminazzo.markdownparser.objects.TableRow;
import it.fulminazzo.markdownparser.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TableNode extends Node {
    private final List<TableRow> tableRows;
    private TableRow titleRow;

    public TableNode(String rawContent) {
        this.tableRows = new ArrayList<>();
        setContent(rawContent);
    }

    public void setContent(String rawContent) {
        if (rawContent == null) return;
        titleRow = null;
        tableRows.clear();
        for (int i = 1; i < Constants.MAX_TABLE_LENGTH; i++) {
            String TABLE_REGEX = Constants.TABLE_REGEX_BASE64.replace("_N", "_" + i);
            Matcher matcher = Pattern.compile(TABLE_REGEX).matcher(rawContent);
            if (matcher.find()) rawContent = Constants.decompressTables(rawContent);
            TABLE_REGEX = Constants.getTableRegex(i);
            matcher = Pattern.compile(TABLE_REGEX).matcher(rawContent);
            if (matcher.find()) {
                rawContent = rawContent.substring(matcher.group().length());
                titleRow = new TableRow(matcher.group(1));
                tableRows.addAll(Arrays.stream(matcher.group(3).split("\n"))
                        .map(TableRow::new)
                        .collect(Collectors.toList()));
                break;
            }
        }
        addNode(new TextBlock(rawContent));
    }

    public List<String> getRowContents(int row) {
        if (row < 0 || row > getRowSize()) throw new ArrayIndexOutOfBoundsException(getRowSize());
        if (titleRow != null)
            if (row > 0) row--;
            else return titleRow.getStringContents();
        return tableRows.get(row).getStringContents();
    }

    public int getRowSize() {
        return tableRows.size() + (titleRow == null ? 0 : 1);
    }

    public List<String> getColumnContents(int column) {
        if (column < 0 || column > getColumnSize()) throw new ArrayIndexOutOfBoundsException(getColumnSize());
        List<String> list = new ArrayList<>();
        if (titleRow != null) list.add(titleRow.getColumnContent(column));
        list.addAll(tableRows.stream().map(s -> s.getColumnContent(column)).collect(Collectors.toList()));
        return list;
    }

    public int getColumnSize() {
        return titleRow != null ? titleRow.getColumnSize() : tableRows.stream()
                .filter(Objects::nonNull)
                .map(TableRow::getColumnSize)
                .findFirst().orElse(0);
    }

    protected String getLongestColumnContent(int column) {
        String longest = "";
        for (String s : getColumnContents(column))
            if (s.length() > longest.length()) longest = s;
        return longest;
    }

    protected Integer[] getLongestColumns() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < getColumnSize(); i++)
            list.add(getLongestColumnContent(i).length());
        return list.toArray(new Integer[0]);
    }

    public List<TableRow> getContents() {
        List<TableRow> contents = new ArrayList<>();
        if (titleRow != null) contents.add(titleRow);
        contents.addAll(tableRows);
        return contents;
    }

    protected String formatRow(TableRow tableRow) {
        String output = "| ";
        Integer[] longestColumns = getLongestColumns();
        for (int i = 0; i < tableRow.getColumnSize(); i++) {
            String content = tableRow.getColumnContent(i);
            content += " ".repeat(longestColumns[i] - content.length());
            output += String.format("%s | ", content);
        }
        if (output.equals("| ")) output += "|";
        return output;
    }

    protected String formatRow(String content) {
        String output = "| ";
        Integer[] longestColumns = getLongestColumns();
        for (Integer longestColumn : longestColumns) {
            content += content.repeat(longestColumn - content.length());
            output += String.format("%s | ", content);
        }
        if (output.equals("| ")) output += "|";
        return output;
    }

    @Override
    public String serialize() {
        String output = "\n";
        if (titleRow != null) {
            output += formatRow(titleRow) + "\n";
            output += formatRow("-") + "\n";
        }
        for (TableRow tableRow : tableRows)
            output += formatRow(tableRow) + "\n";
        return output;
    }

    @Override
    public String getContent() {
        String output = "{\n";
        if (titleRow != null) {
            output += Constants.SEPARATOR + "title: {\n" + Constants.SEPARATOR;
            output += titleRow.toString().replace("\n", "\n" + Constants.SEPARATOR.repeat(2));
            output += "\n" + Constants.SEPARATOR + "},\n";
        }
        output += Constants.SEPARATOR + "contents: {\n";
        for (int i = 0; i < tableRows.size(); i++) {
            output += Constants.SEPARATOR.repeat(2);
            output += tableRows.get(i).toString().replace("\n", "\n" + Constants.SEPARATOR.repeat(2));
            if (i < tableRows.size() - 1) output += ",";
            output += "\n";
        }
        return output + Constants.SEPARATOR + "}\n}";
    }
}