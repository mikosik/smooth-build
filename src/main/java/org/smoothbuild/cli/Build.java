package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.lang.function.base.Name.isLegalName;
import static org.smoothbuild.lang.function.base.Name.name;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.parse.ModuleParser;
import org.smoothbuild.parse.ParsingException;
import org.smoothbuild.task.exec.ExecutionData;
import org.smoothbuild.task.exec.ExecutionException;
import org.smoothbuild.task.exec.SmoothExecutor;
import org.smoothbuild.util.DuplicatesDetector;

import com.google.common.collect.ImmutableList;

public class Build implements Command {
  @Inject
  private Console console;
  @Inject
  private ModuleParser moduleParser;
  @Inject
  private SmoothExecutor smoothExecutor;

  @Override
  public int run(String... functions) {
    List<String> argsWithoutFirst = ImmutableList.copyOf(functions).subList(1, functions.length);
    Set<Name> functionNames = parseArguments(argsWithoutFirst);
    if (functionNames == null) {
      return EXIT_CODE_ERROR;
    }

    try {
      Module module = moduleParser.createModule();
      smoothExecutor.execute(new ExecutionData(functionNames, module));
    } catch (ParsingException | ExecutionException e) {
      return printErrorAndReturnErrorCode(e);
    }

    console.printFinalSummary();
    return console.isProblemReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }

  private int printErrorAndReturnErrorCode(Exception e) {
    String message = e.getMessage();
    if (message != null) {
      console.error(message);
    }
    return EXIT_CODE_ERROR;
  }

  public Set<Name> parseArguments(List<String> args) {
    DuplicatesDetector<Name> duplicatesDetector = new DuplicatesDetector<>();
    for (String argument : args) {
      if (isLegalName(argument)) {
        duplicatesDetector.addValue(name(argument));
      } else {
        console.error("Illegal function name '" + argument + "' passed in command line.");
        return null;
      }
    }

    for (Name name : duplicatesDetector.getDuplicateValues()) {
      console.error("Function " + name + " has been specified more than once.");
      return null;
    }

    return duplicatesDetector.getUniqueValues();
  }
}
