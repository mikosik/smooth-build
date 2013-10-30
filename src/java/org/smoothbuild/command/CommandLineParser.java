package org.smoothbuild.command;

import static org.smoothbuild.command.SmoothContants.DEFAULT_SCRIPT;
import static org.smoothbuild.function.base.Name.isLegalName;
import static org.smoothbuild.function.base.Name.simpleName;

import java.util.List;

import org.smoothbuild.command.err.CommandLineError;
import org.smoothbuild.command.err.IllegalFunctionNameError;
import org.smoothbuild.command.err.NothingToDoError;
import org.smoothbuild.message.listen.ErrorMessageException;

public class CommandLineParser {
  public CommandLineArguments parse(List<String> args) {
    if (args.size() == 0) {
      throw new ErrorMessageException(new NothingToDoError());
    }

    if (args.size() > 1) {
      throw new ErrorMessageException(new CommandLineError(
          "Too many functions. Only one can be specified. (This will change in future version)"));
    }

    String functionString = args.get(0);
    if (!isLegalName(functionString)) {
      throw new ErrorMessageException(new IllegalFunctionNameError(functionString));
    }
    return new CommandLineArguments(DEFAULT_SCRIPT, simpleName(functionString));
  }
}
