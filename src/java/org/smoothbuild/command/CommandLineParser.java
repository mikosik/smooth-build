package org.smoothbuild.command;

import static org.smoothbuild.command.SmoothContants.DEFAULT_SCRIPT;
import static org.smoothbuild.function.base.Name.isLegalSimpleName;
import static org.smoothbuild.function.base.Name.qualifiedName;

import org.smoothbuild.command.err.CommandLineError;
import org.smoothbuild.command.err.IllegalFunctionNameError;
import org.smoothbuild.command.err.NothingToDoError;
import org.smoothbuild.message.message.ErrorMessageException;

public class CommandLineParser {
  public CommandLineArguments parse(String... args) {
    if (args.length == 0) {
      throw new ErrorMessageException(new NothingToDoError());
    }

    if (args.length > 1) {
      throw new ErrorMessageException(new CommandLineError(
          "Too many functions. Only one can be specified. (This will change in future version)"));
    }

    String functionString = args[0];
    if (!isLegalSimpleName(functionString)) {
      throw new ErrorMessageException(new IllegalFunctionNameError(functionString));
    }
    return new CommandLineArguments(DEFAULT_SCRIPT, qualifiedName(functionString));
  }
}
