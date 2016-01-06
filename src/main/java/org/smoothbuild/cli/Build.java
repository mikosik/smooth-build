package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.lang.function.base.Name.isLegalName;
import static org.smoothbuild.lang.function.base.Name.name;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.parse.ModuleParser;
import org.smoothbuild.parse.ParsingException;
import org.smoothbuild.task.exec.ExecutionException;
import org.smoothbuild.task.exec.SmoothExecutor;
import org.smoothbuild.util.DuplicatesDetector;

import com.google.common.collect.ImmutableList;

public class Build implements Command {
  private final Console console;
  private final TempManager tempManager;
  private final ModuleParser moduleParser;
  private final SmoothExecutor smoothExecutor;

  @Inject
  public Build(Console console, TempManager tempManager, ModuleParser moduleParser,
      SmoothExecutor smoothExecutor) {
    this.console = console;
    this.tempManager = tempManager;
    this.moduleParser = moduleParser;
    this.smoothExecutor = smoothExecutor;
  }

  @Override
  public int run(String... functions) {
    List<String> argsWithoutFirst = ImmutableList.copyOf(functions).subList(1, functions.length);
    Set<Name> functionNames = parseArguments(argsWithoutFirst);
    if (functionNames == null) {
      return EXIT_CODE_ERROR;
    }
    tempManager.removeTemps();
    try {
      Map<Name, Function> module = moduleParser.createModule();
      smoothExecutor.execute(functionNames, module);
    } catch (ParsingException | ExecutionException e) {
      return EXIT_CODE_ERROR;
    }

    console.printFinalSummary();
    return console.isErrorReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
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
