package org.smoothbuild.command;

import static org.smoothbuild.function.base.QualifiedName.isValidSimpleName;
import static org.smoothbuild.function.base.QualifiedName.qualifiedName;

import org.smoothbuild.command.err.CommandLineError;
import org.smoothbuild.command.err.IllegalFunctionNameError;
import org.smoothbuild.command.err.NothingToDoError;
import org.smoothbuild.problem.ProblemsListener;

public class CommandLineParser {
  public CommandLineArguments parse(ProblemsListener problemsListeners, String[] args) {
    if (args.length == 0) {
      problemsListeners.report(new NothingToDoError());
      return null;
    }

    if (args.length > 1) {
      problemsListeners.report(new CommandLineError(
          "Too many functions. Only one can be specified. (This will change in future version)"));
      return null;
    }

    String functionString = args[0];
    if (!isValidSimpleName(functionString)) {
      problemsListeners.report(new IllegalFunctionNameError(functionString));
      return null;
    }
    return new CommandLineArguments("build.smooth", qualifiedName(functionString));
  }
}
