package it.fulminazzo.markdownparser;

import it.fulminazzo.markdownparser.nodes.HeaderNode;
import it.fulminazzo.markdownparser.nodes.Node;
import it.fulminazzo.markdownparser.nodes.RootNode;
import it.fulminazzo.markdownparser.objects.NodesList;
import it.fulminazzo.markdownparser.utils.ProgramUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MarkdownParser {
    /*
        /markdownparser read <file>
        /markdownparser copy <file1> <file2>
        /markdownparser optimize <file> # copy file1 file2
        /markdownparser separate <file> # separate file headers in different files
     */
    private static final List<Command> commands = Arrays.asList(
            new Command("read", a -> read(a[0], System.out::println), "file"),
            new Command("copy", a -> copy(a[0], a[1]), "file1", "file2"),
            new Command("optimize", a -> {
                File file = new File(a[0]);
                RootNode node = read(a[0]);
                if (node == null) return;
                System.out.printf("Optimizing %s...%n", file.getName());
                ProgramUtils.executeTimed(() -> {
                    node.write(file);
                    return null;
                }, "Optimized " + file.getName() + ". Time: %time%s");
            }, "file"),
            new Command("separate", a -> {
                String fileName = a[0];
                String originalFileName = fileName;
                String extension = "";
                int indexOfDot = originalFileName.indexOf(".");
                if (indexOfDot != -1) {
                    extension = originalFileName.substring(indexOfDot);
                    originalFileName = originalFileName.substring(0, indexOfDot);
                }
                originalFileName = originalFileName + "-original" + extension;
                RootNode node = copy(fileName, originalFileName);
                if (node == null) return;

                File parent = new File(fileName).getParentFile();

                NodesList headerNodes = node.findNodes(HeaderNode.class);
                if (headerNodes == null || headerNodes.isEmpty()) {
                    write(node, originalFileName);
                    return;
                }
                int maxHeader = headerNodes.stream()
                        .map(n -> ((HeaderNode) n).getHeader())
                        .min(Comparator.comparing(n -> n))
                        .orElse(-1);
                if (maxHeader == -1) {
                    write(node, originalFileName);
                    return;
                }
                List<HeaderNode> nodes = headerNodes.stream()
                        .map(n -> (HeaderNode) n)
                        .filter(n -> n.getHeader() == maxHeader)
                        .collect(Collectors.toList());
                for (HeaderNode headerNode : nodes) {
                    write(headerNode, new File(parent, headerNode.getHeaderText() + ".md").getPath());
                    node.removeRecursive(headerNode);
                }
                write(node, fileName);
            }, "file")
    );

    public static void main(String[] args) {
        //TODO: - `fu.yml` in Test.md
        if (true) {
            return;
        }


        if (args.length == 0) {
            System.err.println("You did not specify enough arguments!");
            System.err.println("Available commands:");
            commands.forEach(System.err::println);
            return;
        }
        Command command = commands.stream().filter(c -> c.equals(args[0])).findFirst().orElse(null);
        if (command == null) {
            System.err.printf("Command %s not found!%n", args[0]);
            return;
        }
        if (args.length - 1 < command.getArguments().length) {
            System.err.println("You did not specify enough arguments!");
            System.err.println(command);
            return;
        }
        command.execute(Arrays.copyOfRange(args, 1, args.length));
    }

    public static String getFileName() {
        try {
            return new File(MarkdownParser.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()).getName();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static RootNode read(String fileName) {
        return read(fileName, null);
    }

    private static RootNode read(String fileName, Consumer<RootNode> function) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.err.printf("File %s not found!%n", fileName);
            return null;
        }
        fileName = file.getName();
        System.out.printf("Reading %s from disk...%n", fileName);
        return ProgramUtils.executeTimed(() -> {
            RootNode rootNode = new RootNode(file);
            if (function != null) function.accept(rootNode);
            return rootNode;
        }, "Read " + fileName + " from disk. Time: %time%s");
    }

    private static RootNode copy(String fileName1, String fileName2) {
        File file1 = new File(fileName1);
        RootNode node = read(fileName1);
        if (node == null) return null;
        File file2 = new File(fileName2);
        if (file2.exists()) {
            System.err.printf("File %s already exists!%n", file2.getName());
            return null;
        }
        write(node, file2.getPath(), String.format("Copying %s to %s...", file1.getName(), file2.getName()),
                "Copied " + file1.getName() + " to " + file2.getName() + ". Time: %time%s");
        return node;
    }

    private static void write(Node node, String fileName) {
        write(node, fileName, null, null);
    }

    private static void write(Node node, String fileName, String startText, String finalText) {
        File file = new File(fileName);
        if (startText == null)
            startText = String.format("Writing to %s...", file.getName());
        if (finalText == null)
            finalText = "Written to " + file.getName() + ". Time: %time%s";
        System.out.println(startText);
        ProgramUtils.executeTimed(() -> {
            node.write(fileName);
            return null;
        }, finalText);
    }
}