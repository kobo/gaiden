package gaiden.command

import gaiden.exception.IllegalOperationException
import groovy.transform.CompileStatic

@CompileStatic
class UnknownCommand extends AbstractCommand {

    final String name = "unknown"

    final boolean onlyGaidenProject = false

    @Override
    void execute(List<String> arguments, OptionAccessor optionAccessor) {
        throw new IllegalOperationException()
    }
}
