package org.smoothbuild.command;

import static org.smoothbuild.command.SmoothContants.DEFAULT_SCRIPT;
import static org.smoothbuild.function.base.Name.isLegalSimpleName;
import static org.smoothbuild.function.base.Name.qualifiedName;

import org.smoothbuild.command.err.CommandLineError;
import org.smoothbuild.command.err.IllegalFunctionNameError;
import org.smoothbuild.command.err.NothingToDoError;
import org.smoothbuild.problem.MessageListener;

public class CommandLineParser {
  public CommandLineArguments parse(MessageListener messages, String... args) {
    if (args.length == 0) {
      messages.report(new NothingToDoError());
      return null;
    }

    if (args.length > 1) {
      messages.report(new CommandLineError(
          "Too many functions. Only one can be specified. (This will change in future version)"));
      return null;
    }

    String functionString = args[0];
    if (!isLegalSimpleName(functionString)) {
      messages.report(new IllegalFunctionNameError(functionString));
      return null;
    }
    return new CommandLineArguments(DEFAULT_SCRIPT, qualifiedName(functionString));
  }
}
