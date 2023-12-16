First parse quotes <quote>
Then parse tables <table>
Finally parse codes <code>

Node {
Node parent;
Node child;
abstract isEmpty();

    getFirstNode()
    
    getLastNode()
}

TextNode {}

SimpleTextNode {}

HeaderNode {
text
content

    setText(String)
    
    setContent(String)
}

CodeNode {
language
code

    setCode(String)
}

TableNode {
rows[]
}