package org.pegdown;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.Tag;
import org.apache.commons.lang3.StringUtils;
import org.parboiled.Context;
import org.parboiled.Rule;
import org.parboiled.annotations.Cached;
import org.parboiled.annotations.DontSkipActionsInPredicates;
import org.parboiled.support.StringBuilderVar;
import org.parboiled.support.StringVar;
import org.parboiled.support.Var;
import org.pegdown.ast.*;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"InfiniteRecursion"})
public class GaidenParser extends Parser {

    private static Pattern MARKDOWN_ATTRIBUTE_PATTERN = Pattern.compile("(1|span|block)");
    private static List<String> CONTAIN_SPAN_ELEMENTS = Arrays.asList("p", "h1", "h2", "h3", "h4", "h5", "h6", "li", "dd", "dt", "td", "th", "legend", "address");

    public GaidenParser() {
        super(ALL & ~HARDWRAPS, PegDownProcessor.DEFAULT_MAX_PARSING_TIME, DefaultParseRunnerProvider);
    }

    public Rule SetextHeading1() {
        return Sequence(
            SetextInline(), push(new GaidenHeaderNode(1, popAsNode())),
            ZeroOrMore(SetextInline(), addAsChild()),
            Sp(),
            Optional(SpecialAttributes(), push(new GaidenHeaderNode((SpecialAttributesNode) pop(), (HeaderNode) pop()))),
            Sp(),
            Newline(), NOrMore('=', 3), Newline()
        );
    }

    public Rule SetextHeading2() {
        return Sequence(
            SetextInline(), push(new HeaderNode(2, popAsNode())),
            ZeroOrMore(SetextInline(), addAsChild()),
            Sp(),
            Optional(SpecialAttributes(), push(new GaidenHeaderNode((SpecialAttributesNode) pop(), (HeaderNode) pop()))),
            Sp(),
            Newline(), NOrMore('-', 3), Newline()
        );
    }

    public Rule SetextInline() {
        return Sequence(
            TestNot(Endline()),
            TestNot(Sp(), Optional(SpecialAttributes()), Sp(), Endline()),
            Inline()
        );
    }

    public Rule AtxHeading() {
        return Sequence(
            FirstOf("######", "#####", "####", "###", "##", "#"),
            push(new HeaderNode(match().length())),
            Sp(),
            OneOrMore(AtxInline(), addAsChild()),
            Sp(),
            ZeroOrMore('#'),
            Sp(),
            Optional(SpecialAttributes(), push(new GaidenHeaderNode((SpecialAttributesNode) pop(), (HeaderNode) pop()))),
            Sp(),
            Newline()
        );
    }

    public Rule AtxInline() {
        return Sequence(
            TestNot(Newline()),
            TestNot(Sp(), ZeroOrMore('#'), Sp(), Optional(SpecialAttributes()), Sp(), Newline()),
            Inline()
        );
    }

    @Cached
    public Rule ExplicitLink(boolean image) {
        return Sequence(
            Spn1(), '(', Sp(),
            LinkSource(),
            Spn1(), FirstOf(LinkTitle(), push("")),
            Sp(), ')',
            FirstOf(SpecialAttributes(), push(new SpecialAttributesNode())),
            push(image ?
                    new GaidenExpImageNode((SpecialAttributesNode) pop(), popAsString(), popAsString(), popAsNode()) :
                    new GaidenExpLinkNode((SpecialAttributesNode) pop(), popAsString(), popAsString(), popAsNode())
            )
        );
    }

    public Rule Reference() {
        Var<ReferenceNode> ref = new Var<>();
        return NodeSequence(
            NonindentSpace(),
            Label(),
            push(ref.setAndGet(new GaidenReferenceNode(popAsNode()))),
            ':',
            Spn1(),
            RefSrc(ref),
            Sp(),
            Optional(RefTitle(ref)),
            Sp(),
            Optional(SpecialAttributes(), ((GaidenReferenceNode) ref.get()).setSpecialAttributesNode((SpecialAttributesNode) pop())),
            Sp(),
            Newline(),
            ZeroOrMore(BlankLine()),
            references.add(ref.get())
        );
    }

    public Rule RefTitle(char open, char close, Var<ReferenceNode> ref) {
        return Sequence(
            open,
            ZeroOrMore(TestNot(close, Sp(), Optional(SpecialAttributes()), Sp(), Newline()), NotNewline(), ANY),
            ref.get().setTitle(match()),
            close
        );
    }

    public Rule FencedCodeBlock() {
        StringBuilderVar text = new StringBuilderVar();
        Var<Integer> markerLength = new Var<>();
        return NodeSequence(
            CodeFence(markerLength),
            TestNot(CodeFence(markerLength)), // prevent empty matches
            ZeroOrMore(BlankLine(), text.append('\n')),
            OneOrMore(TestNot(Newline(), CodeFence(markerLength)), ANY, text.append(matchedChar())),
            Newline(),
            push(new GaidenVerbatimNode(text.appended('\n').getString(), (SpecialAttributesNode) pop())),
            CodeFence(markerLength), drop()
        );
    }

    @Cached
    public Rule CodeFence(Var<Integer> markerLength) {
        Var<String> type = new Var<>();
        Var<SpecialAttributesNode> specialAttributesNodeVar = new Var<>();
        return Sequence(
            FirstOf(NOrMore('~', 3), NOrMore('`', 3)),
            (markerLength.isSet() && matchLength() == markerLength.get()) ||
                (markerLength.isNotSet() && markerLength.set(matchLength())),
            Sp(),
            FirstOf(
                SpecialAttributes(),
                Sequence(
                    ZeroOrMore(TestNot(Newline()), ANY),
                    type.set(match()),
                    specialAttributesNodeVar.setAndGet(new SpecialAttributesNode()).addClass(type.get()),
                    push(specialAttributesNodeVar.get())
                )
            ), // GFM code type identifier
            Sp(),
            Newline()
        );
    }

    public Rule SpecialAttributes() {
        Var<SpecialAttributesNode> specialAttributesNodeVar = new Var<>();
        return Sequence(
            Test('{', Sp(), OneOrMore(FirstOf(IdAttribute(), ClassAttribute()), Sp()), '}'),
            push(specialAttributesNodeVar.setAndGet(new SpecialAttributesNode())),
            '{',
            Sp(),
            OneOrMore(
                FirstOf(IdAttribute(), ClassAttribute()),
                match().startsWith("#") ? specialAttributesNodeVar.get().setId(match().substring(1)) : specialAttributesNodeVar.get().addClass(match().substring(1)),
                Sp()
            ),
            '}'
        );
    }

    public Rule IdAttribute() {
        return Sequence("#", OneOrMore(FirstOf(AnyOf("-_"), NormalChar())));
    }

    public Rule ClassAttribute() {
        return Sequence(".", OneOrMore(FirstOf(AnyOf("-_"), NormalChar())));
    }

    //************* HTML BLOCK ****************

    public Rule HtmlBlock() {
        return NodeSequence(
            FirstOf(HtmlBlockInTags(), HtmlComment(), HtmlBlockSelfClosing()),
            push(createHtmlBlockNode(ext(SUPPRESS_HTML_BLOCKS) ? "" : match())),
            OneOrMore(BlankLine())
        );
    }

    @Cached
    public Rule HtmlTagBlock(StringVar tagName) {
        return FirstOf(NonIndentHtmlTagBlock(tagName), IndentHtmlTagBlock(tagName));
    }

    @Cached
    public Rule NonIndentHtmlTagBlock(StringVar tagName) {
        return Sequence(
            HtmlBlockOpen(tagName),
            TestNot(BlankLine()),
            ZeroOrMore(
                FirstOf(
                    NonIndentHtmlTagBlock(tagName),
                    Sequence(TestNot(NonIndentHtmlBlockClose(tagName)), ANY)
                )
            ),
            NonIndentHtmlBlockClose(tagName)
        );
    }

    @Cached
    public Rule IndentHtmlTagBlock(StringVar tagName) {
        return Sequence(
            HtmlBlockOpen(tagName),
            BlankLine(),
            ZeroOrMore(TestNot(IndentHtmlBlockClose(tagName)), ANY),
            IndentHtmlBlockClose(tagName)
        );
    }

    @DontSkipActionsInPredicates
    public Rule NonIndentHtmlBlockClose(StringVar tagName) {
        return Sequence('<', Spn1(), '/', OneOrMore(Alphanumeric()), match().equals(tagName.get()), Spn1(), '>');
    }

    @DontSkipActionsInPredicates
    public Rule IndentHtmlBlockClose(StringVar tagName) {
        return Sequence(Newline(), '<', Spn1(), '/', OneOrMore(Alphanumeric()), match().equals(tagName.get()), Spn1(), '>');
    }

    public Node createHtmlBlockNode(String text) {
        Source source = new Source(text);
        return createMarkdownInsideHtmlBlockNode(text, 0, source, new SuperNode());
    }

    private Node createMarkdownInsideHtmlBlockNode(String text, int pos, Source source, SuperNode superNode) {
        Element markdownElement = source.getNextElement(pos, "markdown", MARKDOWN_ATTRIBUTE_PATTERN);
        if (markdownElement == null) {
            superNode.getChildren().add(new HtmlBlockNode(text.substring(pos)));
            return superNode;
        }

        superNode.getChildren().add(new HtmlBlockNode(text.substring(pos, markdownElement.getBegin())));

        String indent = getIndent(text, markdownElement);
        Tag endTag = getEndTag(text, source, markdownElement, indent);
        String innerText = getInnerText(text, markdownElement, endTag, indent);
        List<Node> children = parseInnerText(innerText);

        superNode.getChildren().add(new MarkdownInsideHtmlBlockNode(
            markdownElement.getStartTag().toString(),
            children,
            endTag.toString()
        ));

        return createMarkdownInsideHtmlBlockNode(text, endTag.getEnd(), source, superNode);
    }

    private List<Node> parseInnerText(String innerText) {
        Context<Object> context = getContext();
        RootNode innerRoot = parseInternal(innerText.toCharArray());
        setContext(context);
        return withIndicesShifted(innerRoot, (Integer) peek()).getChildren();
    }

    private String getInnerText(String text, Element markdownElement, Tag endTag, String indent) {
        StringBuilder innerTextBuilder = new StringBuilder();
        String innerText = text.substring(markdownElement.getStartTag().getEnd(), endTag.getBegin());

        if (CONTAIN_SPAN_ELEMENTS.contains(markdownElement.getName())) {
            innerTextBuilder.append(innerText.trim().replaceAll("\n", " "));
            if (markdownElement.getAttributeValue("markdown").equals("block")) {
                innerTextBuilder.append("\n\n");
            }
        } else {
            innerTextBuilder.append(removeIndent(innerText, indent));
            innerTextBuilder.append("\n\n");
        }

        return innerTextBuilder.toString();
    }

    private Tag getEndTag(String text, Source source, Element markdownElement, String indent) {
        if (indent != null) {
            Pattern endTagPattern = Pattern.compile("\n(" + indent + "</" + markdownElement.getName() + "\\s*>)");
            Matcher endTagMatcher = endTagPattern.matcher(text);
            if (endTagMatcher.find(markdownElement.getStartTag().getEnd())) {
                return source.getNextEndTag(endTagMatcher.start(1), markdownElement.getStartTag().getName());
            }
        }

        return markdownElement.getEndTag();
    }

    private String removeIndent(String text, String indent) {
        if (StringUtils.isEmpty(indent)) {
            return text;
        }

        return text.replaceAll("^" + indent, "").replaceAll("\n" + indent, "\n");
    }

    private String getIndent(String text, Element markdownElement) {
        Pattern beginPattern = Pattern.compile("^( *)" + markdownElement.getStartTag().toString());
        Matcher beginMatcher = beginPattern.matcher(text);
        if (beginMatcher.find()) {
            return beginMatcher.group(1);
        }

        Pattern innerPattern = Pattern.compile("\n( +)$");
        Matcher innerMatcher = innerPattern.matcher(text.substring(0, markdownElement.getBegin()));
        if (innerMatcher.find()) {
            return innerMatcher.group(1);
        }

        return null;
    }
}
