# MarkdownParser
As the name implies, this project is a Java Markdown parser.
It provides an [Object Oriented API](#nodes) which allows to work and manipulate Markdown elements.

## How does it work
MarkdownParser takes as input a string (or raw data, check out [RootNode](#rootnode) for more)
and returns a group of nodes representing each found element.
It uses regular expressions to search and individuate valid Markdown items
and converting them into a similar HTML tag format.

For example, say the given input is:
```markdown
# Hello world
This project is great!
```
The `HEADER1` regular expression will convert it into its tagged form:
```html
<HEADER1>IyBIZWxsbyB3b3JsZApUaGlzIHByb2plY3QgaXMgZ3JlYXQh</HEADER1>
```
Where the content of the tags is the Base64 encoded version of Markdown.
It will later be used by other nodes that will decode it and repeat this process, 
until a simple text is met (in which case, it will be used a [SimpleTextNode](#simpletextnode)).

# Nodes
To read and translate Markdown data into MarkdownParser API, many nodes are provided. 

However, when starting to read data, you should always start with [RootNode](#rootnode).

| Nodes                             |
|-----------------------------------|
| [Node](#node)                     |
| [RootNode](#rootnode)             |
| [SimpleTextNode](#simpletextnode) | 
| [TextNode](#textnode)             |
| [LinkNode](#linknode)             |
| [TextBlock](#textblock)           |
| [HeaderNode](#headernode)         |
| [ListNode](#listnode)             |
| [CodeNode](#codenode)             |
| [CommentNode](#commentnode)       |
| [QuoteNode](#quotenode)           |
| [TableNode](#tablenode)           |

## Node
[Node](/src/main/java/it/fulminazzo/markdownparser/nodes/Node.java) 
is the basic implementation for every node of the program.
A node is simply an object that might contain some content
(in which case, it can be set with the `setContent(String)` method),
and has:
- a parent that can be retrieved with `getParent()`;
- siblings, that can be retrieved with `getPrev()` and `getNext()`;
- a child (with its siblings),
  that can be retrieved
  with `getChild()` or with `getChildren()` to get a full [NodesList](/src/main/java/it/fulminazzo/markdownparser/objects/NodesList.java).

Basically, a Node works pretty much like a [Tree data structure](https://en.wikipedia.org/wiki/Tree_(data_structure)).

It also provides some useful functions to work with:
- `serialize()`: converts the current node into its corresponding Markdown format;
- `serializeChildren()`: calls `serialize()` for every child. Usually, this gets called in `serialize()` by default;
- `toString()`: prints the node name and its [ContentMap](/src/main/java/it/fulminazzo/markdownparser/objects/ContentMap.java);
- `write(File)` and `write(OutputStream)`: respectively, call the `serialize()` method and write the result on the given file or stream.

## RootNode
RootNode is the main starting point for reading any Markdown data.
It is just a wrapper that contains the read data from one of its constructors:
```java
public RootNode(File file) throws IOException;

public RootNode(InputStream inputStream) throws IOException;

public RootNode(String rawContent);
```

## SimpleTextNode
[SimpleTextNode](/src/main/java/it/fulminazzo/markdownparser/nodes/SimpleTextNode.java)
## TextNode
[TextNode](/src/main/java/it/fulminazzo/markdownparser/nodes/TextNode.java)
## LinkNode
[LinkNode](/src/main/java/it/fulminazzo/markdownparser/nodes/LinkNode.java)
## TextBlock
[TextBlock](/src/main/java/it/fulminazzo/markdownparser/nodes/TextBlock.java)
## HeaderNode
[HeaderNode](/src/main/java/it/fulminazzo/markdownparser/nodes/HeaderNode.java)
## ListNode
[ListNode](/src/main/java/it/fulminazzo/markdownparser/nodes/ListNode.java)
## CodeNode
[CodeNode](/src/main/java/it/fulminazzo/markdownparser/nodes/CodeNode.java)
## CommentNode
[CommentNode](/src/main/java/it/fulminazzo/markdownparser/nodes/CommentNode.java)
## QuoteNode
[QuoteNode](/src/main/java/it/fulminazzo/markdownparser/nodes/QuoteNode.java)
## TableNode
[TableNode](/src/main/java/it/fulminazzo/markdownparser/nodes/TableNode.java)