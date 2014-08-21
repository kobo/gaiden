package org.pegdown;

import org.parboiled.Rule;
import org.parboiled.annotations.Cached;
import org.parboiled.common.ArrayBuilder;
import org.parboiled.support.StringBuilderVar;
import org.parboiled.support.StringVar;
import org.parboiled.support.Var;
import org.pegdown.ast.*;

@SuppressWarnings({"InfiniteRecursion"})
public class GaidenParser extends Parser {

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

    public Rule Block() {
        return Sequence(
            ZeroOrMore(BlankLine()),
            FirstOf(new ArrayBuilder<Rule>()
                    .add(plugins.getBlockPluginRules())
                    .add(BlockQuote(), Verbatim())
                    .addNonNulls(ext(ABBREVIATIONS) ? Abbreviation() : null)
                    .add(Reference(), HorizontalRule(), Heading(), OrderedList(), BulletList(), MarkdownInsideHtmlBlock(), HtmlBlock())
                    .addNonNulls(ext(TABLES) ? Table() : null)
                    .addNonNulls(ext(DEFINITIONS) ? DefinitionList() : null)
                    .addNonNulls(ext(FENCED_CODE_BLOCKS) ? FencedCodeBlock() : null)
                    .add(Para(), Inlines())
                    .get()
            )
        );
    }

    public Rule MarkdownInsideHtmlBlock() {
        return NodeSequence(
            MarkdownInsideHtmlBlockInTags(),
            OneOrMore(BlankLine())
        );
    }

    public Rule MarkdownInsideHtmlBlockInTags() {
        StringVar tagName = new StringVar();
        return Sequence(
            Test(MarkdownInsideHtmlBlockOpen(tagName)), // get the type of tag if there is one
            MarkdownInsideHtmlTagBlock(tagName) // specifically match that type of tag
        );
    }

    @Cached
    public Rule MarkdownInsideHtmlTagBlock(StringVar tagName) {
        Var<String> startTag = new Var<>();
        StringVar insideBlock = new StringVar();
        return Sequence(
            MarkdownInsideHtmlBlockOpen(tagName),
            startTag.set(match()),
            ZeroOrMore(
                FirstOf(
                    HtmlTagBlock(tagName),
                    Sequence(TestNot(HtmlBlockClose(tagName)), ANY)
                )
            ),
            insideBlock.append(match()),
            HtmlBlockClose(tagName),
            push(new MarkdownInsideHtmlBlockNode(tagName.get(), startTag.get(), withIndicesShifted(parseInternal(insideBlock.appended("\n").get().toCharArray()), (Integer) peek()).getChildren()))
        );
    }

    public Rule MarkdownInsideHtmlBlockOpen(StringVar tagName) {
        Var<Boolean> hasMarkdownAttr = new Var<>();
        return Sequence('<', Spn1(), DefinedHtmlTagName(tagName), Spn1(), OneOrMore(FirstOf(MarkdownAttribute(hasMarkdownAttr), HtmlAttribute())), hasMarkdownAttr.isSet(), '>');
    }

    public Rule MarkdownAttribute(Var<Boolean> hasMarkdownAttr) {
        return Sequence(
            "markdown",
            Spn1(),
            '=',
            Spn1(),
            FirstOf("1", "\"1\"", "'1'", "\"span\"", "'span'", "\"block\"", "'block'"),
            Spn1(),
            hasMarkdownAttr.set(true)
        );
    }
}
