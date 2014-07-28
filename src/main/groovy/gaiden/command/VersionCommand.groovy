package gaiden.command

import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

@Component
@CompileStatic
class VersionCommand extends AbstractCommand {

    final String name = "version"

    final boolean onlyGaidenProject = false

    @Override
    void execute(List<String> arguments, OptionAccessor optionAccessor) {
        def properties = new Properties()
        getClass().getResourceAsStream("/build-receipt.properties").withStream { InputStream stream ->
            properties.load(stream)
        }
        println """\
             _     _
            (_)   | |
  __ _  __ _ _  __| | ___ _ __
 / _` |/ _` | |/ _` |/ _ \\ '_ \\
| (_| | (_| | | (_| |  __/ | | |
 \\__, |\\__,_|_|\\__,_|\\___|_| |_|
  __/ |
 |___/

Version:    ${properties.version}
Revision:   ${properties.revision}
Build date: ${properties.buildDate}

OS:         ${System.properties["os.name"]} ${System.properties["os.version"]}
JVM:        ${System.properties["java.version"]}
Groovy:     ${GroovySystem.getVersion()}\
"""
    }
}
