package it.fulminazzo.markdownparser.nodes;

import it.fulminazzo.markdownparser.enums.Tag;
import it.fulminazzo.markdownparser.objects.ContentMap;
import it.fulminazzo.markdownparser.objects.TableRow;
import it.fulminazzo.markdownparser.utils.Constants;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TableNode extends TagNode {
    private List<TableRow> tableRows;
    private TableRow titleRow;

    public TableNode() {
        this(null);
    }

    public TableNode(String rawContent) {
        super(rawContent, Tag.getTableValues());
    }

    @Override
    public void setContents(String rawContent) {
        if (rawContent == null) return;
        titleRow = null;
        if (tableRows == null) tableRows = new ArrayList<>();
        tableRows.clear();
        for (int i = 1; i <= Constants.getMaxTableLength(); i++) {
            String TABLE_REGEX = Constants.getTableRegex(i);
            Matcher matcher = Pattern.compile(TABLE_REGEX).matcher(rawContent);
            if (matcher.find()) {
                titleRow = new TableRow(matcher.group(1));
                tableRows.addAll(Arrays.stream(matcher.group(3).split("\n"))
                        .map(TableRow::new)
                        .collect(Collectors.toList()));
                break;
            }
        }
        getContentMap();
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
        while (output.endsWith(" ")) output = output.substring(0, output.length() - 1);
        return output;
    }

    protected String formatRow(String content) {
        String output = "|";
        Integer[] longestColumns = getLongestColumns();
        for (Integer longestColumn : longestColumns) {
            content += content.repeat(longestColumn - content.length() + 2);
            output += String.format("%s|", content);
        }
        if (output.equals("|")) output += "|";
        return output;
    }

    @Override
    protected ContentMap getContentMap() {
        ContentMap map =  super.getContentMap().set("title", titleRow.toString());
        String output = "[\n";
        for (int i = 0; i < tableRows.size(); i++) {
            output += Constants.SEPARATOR;
            output += tableRows.get(i).toString().replace("\n", "\n" + Constants.SEPARATOR);
            if (i < tableRows.size() - 1) output += ",";
            output += "\n";
        }
        output += "]";
        if (!output.equals("[\n\n]")) map.put("contents", output);
        return map;
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
        return output + "\n";
    }
}