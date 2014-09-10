
package gaiden.markdown

import groovy.transform.CompileStatic
import org.pegdown.DefaultVerbatimSerializer
import org.pegdown.Printer
import org.pegdown.ast.GaidenVerbatimNode
import org.pegdown.ast.VerbatimNode

@CompileStatic
class GaidenVerbatimSerializer extends DefaultVerbatimSerializer {

    @Override
    void serialize(VerbatimNode node, Printer printer) {
        printer.println().print("<pre><code")

        if (node instanceof GaidenVerbatimNode) {
            if (node.specialAttributesNode?.id) {
                printAttributes(printer, [id: node.specialAttributesNode.id])
            }
            printAttributes(printer, [class: node.specialAttributesNode?.classes?.join(" ") ?: "nohighlight"])
        } else {
            printAttributes(printer, [class: node.getType() ?: "nohighlight"])
        }

        printer.print(">")
        String text = node.text
        // print HTML breaks for all initial newlines
        while (text.charAt(0) == '\n') {
            printer.print("<br/>")
            text = text.substring(1)
        }
        printer.printEncoded(text)
        printer.print("</code></pre>")
    }

    private static void printAttributes(final Printer printer, Map<String, String> attributes) {
        attributes.each {
            printer.print(' ').print(it.key).print('=').print('"').print(it.value).print('"')
        }
    }
}
