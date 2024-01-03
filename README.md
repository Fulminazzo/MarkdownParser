# MarkdownParser
As the name implies, this project is a **Java Markdown parser**.
It provides an [Object Oriented API](#nodes) which allows to **work** with and **manipulate Markdown elements**,
as well as a simple command interface to test and work with files.

Here are the main commands:
- `read <file>`: reads and parses the specified file, showing the output in terminal;
- `copy <file1> <file2>`: reads and parses file `file1` and writes the output to `file2`;
- `optimize <file>`: optimizes the specified file by reading, parsing and removing unnecessary spaces, new lines or quotes;
- `separate <file>`: searches for **headers** in the specified file after parsing it.
  If any is found, the lower ones will be saved in different files, and the main file is overwritten.
  Say you have `main.md` file containing:
  ```markdown
  Are you ready to learn the power of MarkdownParser?
  
  # Sure
  I am excited!
  
  # No
  I still need more time...
  ```
  Then, three files will be created:
  - `Sure.md`, with contents `I am excited!`;
  - `No.md`, with contents `I still need more time...`;
  - `main.md`, will be overwritten with contents `Are you ready to learn the power of MarkdownParser?`.

## How does it work
**MarkdownParser** takes as input a **string** (or **raw data**, check out [RootNode](#rootnode) for more)
and returns a **group of nodes** representing each found element.
It uses **regular expressions** to search and individuate valid Markdown items
and **converting** them into a similar HTML **tag format**.

For example, say the given input is:
```markdown
# Hello world
This project is great!
```
The `HEADER1` regular expression will convert it into its tagged form:
```html
<HEADER1>IyBIZWxsbyB3b3JsZApUaGlzIHByb2plY3QgaXMgZ3JlYXQh</HEADER1>
```
Where the content of the tags is the **Base64 encoded version of the Markdown form**.
It will later be used by other nodes that will **decode it** and **repeat this process**, 
until a simple text is met (in which case, it will be used a [SimpleTextNode](#simpletextnode)).

# API
To start using the API, you can import **MarkdownParser** either from Maven or Gradle:
- **Maven**:
```xml
<repositories>
  <repository>
    <id>fulminazzo-repository</id>
    <url>https://repo.fulminazzo.it/releases</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>it.fulminazzo</groupId>
    <artifactId>MarkdownParser</artifactId>
    <version>1.0</version>
  </dependency>
</dependencies>
```
- **Gradle**:
```groovy
repositories {
    maven { url = "https://repo.fulminazzo.it/releases" }
}

dependencies {
  implementation 'it.fulminazzo.MarkdownParser:1.0'
}
```

## Nodes
To read and translate **Markdown** data into **MarkdownParser API**, many nodes are provided. 

When starting to read data, you should always start with [RootNode](#rootnode).

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

### Node
[Node](/src/main/java/it/fulminazzo/markdownparser/nodes/Node.java) 
is the **basic implementation** for every node of the program.
A node is simply an object that might contain some **content**
(in which case, it can be set with the `setContent(String)` method),
and has:
- a **parent** that can be retrieved with `getParent()`;
- **siblings**, that can be retrieved with `getPrev()` and `getNext()`;
- a **child** (with its siblings),
  that can be retrieved
  with `getChild()` or with `getChildren()` to get a full [NodesList](/src/main/java/it/fulminazzo/markdownparser/objects/NodesList.java).

Basically, a node works pretty much like a [Tree data structure](https://en.wikipedia.org/wiki/Tree_(data_structure)).

It also provides some useful functions to work with:
- `serialize()`: **converts the current node** into its corresponding **Markdown format**;
- `serializeChildren()`: calls `serialize()` for every **child**. Usually, this gets called in `serialize()` by default;
- `toString()`: prints the **node name** and its [ContentMap](/src/main/java/it/fulminazzo/markdownparser/objects/ContentMap.java);
- `write(File)` and `write(OutputStream)`: respectively call the `serialize()` method and **write the result** on the given **file** or **stream**.

### RootNode
[RootNode](/src/main/java/it/fulminazzo/markdownparser/nodes/RootNode.java) 
is the main starting point for **reading** any **Markdown data**.
It is just a **wrapper** that contains the read data from one of its constructors:
```java
public RootNode(File file) throws IOException;

public RootNode(InputStream inputStream) throws IOException;

public RootNode(String rawContent);
```

### SimpleTextNode
[SimpleTextNode](/src/main/java/it/fulminazzo/markdownparser/nodes/SimpleTextNode.java)
represents a **simple text** in Markdown with **no formatting**.
For example, in:
```markdown
# Hello world
This project is great!
```
`This project is great!` will be saved in a **SimpleTextNode**, since it has nothing special to it.

However, say it was:
```markdown
# Hello world
This project is **great!**
```
In this case, there would be two **SimpleTextNodes**: `This project is ` and `great!`,
that will be encapsulated in a [TextNode](#textnode).

### TextNode
[TextNode](/src/main/java/it/fulminazzo/markdownparser/nodes/TextNode.java)
represents a **text** that **supports formatting**.
Checkout [TextType](/src/main/java/it/fulminazzo/markdownparser/enums/TextType.java)
to understand which formats are available.

Say you have:
```markdown
# Hello world
This project is **great!**
```
Then, `**great!**`
will be saved as a **TextNode** of type [Strong](/src/main/java/it/fulminazzo/markdownparser/enums/TextType.java)
and its content (`great!`) will become its child in the form of [SimpleTextNode](#simpletextnode):
```
TextNode: {
    text-type: STRONG,
    children: {
        SimpleTextNode: great!
    }
}
```

### LinkNode
[LinkNode](/src/main/java/it/fulminazzo/markdownparser/nodes/LinkNode.java)
represents a **simple link**.
It supports **hover text**, meaning that both these formats will be valid:
```markdown
[Good Java Project](https://github.com/Fulminazzo/MarkdownParser)
[Good Java Project](https://github.com/Fulminazzo/MarkdownParser "This project is great!")
```

### TextBlock
A [TextBlock](/src/main/java/it/fulminazzo/markdownparser/nodes/TextBlock.java)
is just a **wrapper** node for text blocks.
A **text block is** considered as such when it is **separated** by a **pair of new lines** (`\n\n`).

It has no special method for editing its contents, apart from the ones already discussed in the [Node section](#node).

### HeaderNode
[HeaderNode](/src/main/java/it/fulminazzo/markdownparser/nodes/HeaderNode.java)
represents a **header** with its contents.
A **content of a header** is decided when a header of **same length** is met or the **end of file** is reached.

So, for example:
```markdown
# Hello World
This project is great!
```
Will be loaded as:
```
HeaderNode: {
    header-size: 1,
    header: Hello World,
    children: {
        TextBlock: {
            children: {
                TextNode: {
                    children: {
                        SimpleTextNode: This project is great!
                    }
                }
            }
        }
    }
}
```
But say you had:
```markdown
# Hello World
This project is great!

# Another paragraph
Don't you agree?
```
In this case, since the **two headers** are of the same size, they will be loaded as **siblings**:
```
HeaderNode: {
    header-size: 1,
    header: Hello World,
    children: {
        TextBlock: {
            children: {
                TextNode: {
                    children: {
                        SimpleTextNode: This project is great!
                    }
                }
            }
        }
    }
},
HeaderNode: {
    header-size: 1,
    header: Another paragraph,
    children: {
        TextBlock: {
            children: {
                TextNode: {
                    children: {
                        SimpleTextNode: "Don't you agree?"
                    }
                }
            }
        }
    }
}
```

### ListNode
[ListNode](/src/main/java/it/fulminazzo/markdownparser/nodes/ListNode.java)
represents a **list block**.
Every item of the list will be loaded as [ListElements](/src/main/java/it/fulminazzo/markdownparser/nodes/ListElement.java),
which are just another **wrapper** containing **any other node** (even another ListNode).

Therefore, to **edit the contents** of a **ListNode**, you will have to access and modify its **ListElement** children.

### CodeNode
[CodeNode](/src/main/java/it/fulminazzo/markdownparser/nodes/CodeNode.java)
represents a **code block**.
It supports both **single** and **triple quotes**, as well as **multi-lines** blocks:
```markdown
    `supports this`

    ```and this```

    ```java
        System.out.println("Even this!"
    ```
```
**NOTE**: There is no operation to recognize the validity of the specified language, 
so anything specified in the correct format will be taken as such.

### CommentNode
[CommentNode](/src/main/java/it/fulminazzo/markdownparser/nodes/CommentNode.java)
represents a **comment block**.
It supports both **Markdown** and **HTML comments**:
```markdown
[//]: # (This is supported)
<!-- This is also supported
and allows for multilines!-->
```
By default, **CommentNode** will be **shown** when calling the `serialize()` method on them.
This is because **MarkdownParser**'s primary focus is to **programmatically create Markdown text**.
However, if you want to **disable comments**, use `CommentNode#setVisible(boolean)`.

### QuoteNode
[QuoteNode](/src/main/java/it/fulminazzo/markdownparser/nodes/QuoteNode.java)
represents a **quote block**.
A quote ends when a **pair of new lines** (`\n\n`) is **found**, regardless of the spaces between them.

For example:
```markdown
Quoting Albert Einstein:
> Imagination is more important than knowledge.
For knowledge is limited,
> whereas imagination embraces the entire world, 
stimulating progress, giving birth to evolution.

End of the quote.
```

Will only load as quote:
```
QuoteNode: {
    content: 
    > Imagination is more important than knowledge.
    > For knowledge is limited,
    > whereas imagination embraces the entire world,
    > stimulating progress, giving birth to evolution.
}
```

### TableNode
[TableNode](/src/main/java/it/fulminazzo/markdownparser/nodes/TableNode.java)
represents a **table**.
In **MarkdownParser**, only a **maximum number of columns are supported**.
You can check them
by invoking `Constants#getMaxTableLength()` from the [Constants class](/src/main/java/it/fulminazzo/markdownparser/utils/Constants.java).

Every **table row** will be loaded as a [TableRow](/src/main/java/it/fulminazzo/markdownparser/objects/TableRow.java)
and will be **obtainable** using `TableNode#getTableRows()`.
A **TableRow** loads its contents as [Nodes](#node),
so you will have to access them using `TableRow#getContents()` to modify the row itself.

Also note that **TableNode** has a special row for the **titles**,
which is retrievable using `TableNode#getTitleRow()`.