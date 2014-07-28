package gaiden.command

import gaiden.exception.IllegalOperationException
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class UnknownCommand extends AbstractCommand {

    final String name = "unknown"

    final boolean onlyGaidenProject = false

    @Override
    void execute(List<String> arguments, OptionAccessor optionAccessor) {
        throw new IllegalOperationException()
    }
}
