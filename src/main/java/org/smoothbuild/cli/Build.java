package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.DEFAULT_SCRIPT;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.lang.function.base.Name.isLegalName;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.nativ.NativeLibraryLoader.loadBuiltinFunctions;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.parse.ModuleLoader;
import org.smoothbuild.parse.ParsingException;
import org.smoothbuild.task.exec.ExecutionException;
import org.smoothbuild.task.exec.SmoothExecutor;
import org.smoothbuild.util.DuplicatesDetector;

import com.google.common.collect.ImmutableList;

public class Build {
  private final Console console;
  private final TempManager tempManager;
  private final ModuleLoader moduleLoader;
  private final SmoothExecutor smoothExecutor;
  private final Functions functions;

  @Inject
  public Build(Console console, TempManager tempManager, ModuleLoader moduleLoader,
      SmoothExecutor smoothExecutor, Functions functions) {
    this.console = console;
    this.tempManager = tempManager;
    this.moduleLoader = moduleLoader;
    this.smoothExecutor = smoothExecutor;
    this.functions = functions;
  }

  public int run(String... names) {
    try {
      List<String> argsWithoutFirst = ImmutableList.copyOf(names).subList(1, names.length);
      Set<Name> functionNames = parseArguments(argsWithoutFirst);
      loadBuiltinFunctions(functions);
      tempManager.removeTemps();
      moduleLoader.loadFunctions(DEFAULT_SCRIPT);
      smoothExecutor.execute(functionNames);
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
        throw new ExecutionException();
      }
    }

    for (Name name : duplicatesDetector.getDuplicateValues()) {
      console.error("Function " + name + " has been specified more than once.");
      throw new ExecutionException();
    }
    Set<Name> result = duplicatesDetector.getUniqueValues();
    if (result.isEmpty()) {
      console.error("Specify at least one function to be executed.");
      throw new ExecutionException();
    }
    return result;
  }
}
