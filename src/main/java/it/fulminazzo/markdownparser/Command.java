package it.fulminazzo.markdownparser;

import lombok.Getter;

import java.util.function.Consumer;

class Command {
    private final String name;
    @Getter
    private final String[] arguments;
    private final Consumer<String[]> function;

    Command(String name, Consumer<String[]> function, String... arguments) {
        this.name = name;
        this.arguments = arguments == null ? new String[0] : arguments;
        this.function = function;
    }

    public void execute(String[] args) {
        function.accept(args);
    }

    public boolean equals(String name) {
        return name != null && name.equalsIgnoreCase(this.name);
    }

    @Override
    public String toString() {
        String output = "java -jar %file-name% " + name;
        for (String argument : arguments) output += String.format(" <%s>", argument);
        return output.replace("%file-name%", MarkdownParser.getFileName());
    }
}
