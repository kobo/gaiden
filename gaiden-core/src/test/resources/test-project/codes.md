# Codes

```
code block
```

```type
code block
```

``` type
code block
```

``` {#code1}
code block
```

``` { #code2 .type }
code block
```

```groovy
public class MarkdownInsideHtmlBlockNode extends SuperNode {
    private String beginTag;
    private String endTag;

    public MarkdownInsideHtmlBlockNode(String beginTag, List<Node> children, String endTag) {
        super(children);
        this.beginTag = beginTag;
        this.endTag = endTag;
    }

    public String getBeginTag() {
        return beginTag;
    }

    public String getEndTag() {
        return endTag;
    }
}
```
