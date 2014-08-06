package gaiden

import groovy.transform.CompileStatic
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.nio.file.Paths

@Component
@CompileStatic
class PagesParser {

    private List<PageEntry> pageEntries = []

    @Autowired
    GaidenConfig gaidenConfig

    List<PageEntry> parse(String text) {
        CompilerConfiguration configuration = new CompilerConfiguration()
        configuration.scriptBaseClass = DelegatingScript.name
        configuration.addCompilationCustomizers(new ASTTransformationCustomizer(new PagesAstTransformation()))
        GroovyShell shell = new GroovyShell(this.class.classLoader, new Binding(), configuration)
        DelegatingScript script = shell.parse(text) as DelegatingScript
        script.setDelegate(this)
        script.run()
        return pageEntries
    }

    def methodMissing(String name, args) {
        def path = gaidenConfig.projectDirectory.resolve(Paths.get(name))
        def metadata = args.find { it instanceof Map } ?: Collections.emptyMap()
        pageEntries << new PageEntry(path: path, metadata: metadata as Map<String, String>)

        def closure = args.find { it instanceof Closure } as Closure
        if (closure) {
            closure.delegate = this
            closure.call()
        }
    }
}
