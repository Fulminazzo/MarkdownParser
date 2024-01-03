package it.fulminazzo.markdownparser.nodes;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class TableNodeTest {

    @Test
    public void testMultiTableContent() {
        File file = new File("build/resources/test/table-test.md");
        String content = readFromFile(file);
        TableNode tableNode = new TableNode(content);
        assertEquals(content, tableNode.serialize());
    }

    private String readFromFile(File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            StringBuilder resultStringBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) resultStringBuilder.append(line).append("\n");
            }
            return resultStringBuilder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}