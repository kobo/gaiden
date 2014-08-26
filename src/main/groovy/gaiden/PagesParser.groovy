/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gaiden

import groovy.transform.CompileStatic
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer

import java.nio.file.Path
import java.nio.file.Paths

@CompileStatic
class PagesParser {

    private List<PageReference> pageReferences = []
    private int baseLevel = 0

    Path sourceDirectory

    List<PageReference> parse(String text) {
        CompilerConfiguration configuration = new CompilerConfiguration()
        configuration.scriptBaseClass = DelegatingScript.name
        configuration.addCompilationCustomizers(new ASTTransformationCustomizer(new PagesAstTransformation()))
        GroovyShell shell = new GroovyShell(new Binding(), configuration)
        DelegatingScript script = shell.parse(text) as DelegatingScript
        script.setDelegate(this)
        script.run()
        pageReferences
    }

    def methodMissing(String name, args) {
        def path = sourceDirectory.resolve(Paths.get(name))
        def metadata = args.find { it instanceof Map } ?: Collections.emptyMap()
        pageReferences << new PageReference(path: path, metadata: metadata as Map<String, String>, baseLevel: baseLevel)

        def closure = args.find { it instanceof Closure } as Closure
        if (closure) {
            closure.delegate = this
            baseLevel++
            closure.call()
            baseLevel--
        }
    }
}
