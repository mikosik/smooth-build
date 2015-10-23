package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.lang.function.base.Name.isLegalName;
import static org.smoothbuild.lang.function.base.Name.name;

import java.io.PrintStream;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.message.base.Console;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.parse.ModuleParser;
import org.smoothbuild.parse.ParsingException;
import org.smoothbuild.task.exec.ExecutionData;
import org.smoothbuild.task.exec.SmoothExecutorPhase;
import org.smoothbuild.util.DuplicatesDetector;

import com.google.common.collect.ImmutableList;

public class Build implements Command {
  @Inject
  @Console
  private PrintStream console;
  @Inject
  private UserConsole userConsole;
  @Inject
  private ModuleParser moduleParser;
  @Inject
  private SmoothExecutorPhase smoothExecutorPhase;

  @Override
  public int run(String... functions) {
    List<String> argsWithoutFirst = ImmutableList.copyOf(functions).subList(1, functions.length);
    Set<Name> functionNames = parseArguments(argsWithoutFirst);
    if (functionNames == null) {
      return EXIT_CODE_ERROR;
    }

    if (!userConsole.isProblemReported()) {
      try {
        Module module = moduleParser.createModule();
        smoothExecutorPhase.execute(new ExecutionData(functionNames, module));
      } catch (ParsingException e) {
        String message = e.getMessage();
        if (message != null) {
          console.println(message);
        }
        return EXIT_CODE_ERROR;
      }
    }

    userConsole.printFinalSummary();
    return userConsole.isProblemReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }

  public Set<Name> parseArguments(List<String> args) {
    DuplicatesDetector<Name> duplicatesDetector = new DuplicatesDetector<>();
    for (String argument : args) {
      if (isLegalName(argument)) {
        duplicatesDetector.addValue(name(argument));
      } else {
        console.println("error: Illegal function name '" + argument + "' passed in command line.");
        return null;
      }
    }

    for (Name name : duplicatesDetector.getDuplicateValues()) {
      console.println("error: Function " + name + " has been specified more than once.");
      return null;
    }

    return duplicatesDetector.getUniqueValues();
  }
}
