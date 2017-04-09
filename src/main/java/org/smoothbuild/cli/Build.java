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
import org.smoothbuild.parse.Maybe;
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

  @Inject
  public Build(Console console, TempManager tempManager, ModuleLoader moduleLoader,
      SmoothExecutor smoothExecutor) {
    this.console = console;
    this.tempManager = tempManager;
    this.moduleLoader = moduleLoader;
    this.smoothExecutor = smoothExecutor;
  }

  public int run(String... names) {
    List<String> argsWithoutFirst = ImmutableList.copyOf(names).subList(1, names.length);
    Maybe<Set<Name>> functionNames = parseArguments(argsWithoutFirst);
    if (!functionNames.hasResult()) {
      for (Object error : functionNames.errors()) {
        console.rawError(error);
      }
      return EXIT_CODE_ERROR;
    }
    tempManager.removeTemps();
    try {
      Functions functions = loadFunctions();
      smoothExecutor.execute(functions, functionNames.result());
    } catch (ParsingException | ExecutionException e) {
      return EXIT_CODE_ERROR;
    }

    console.printFinalSummary();
    return console.isErrorReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }

  private Functions loadFunctions() {
    Functions builtinFunctions = loadBuiltinFunctions();
    Functions definedFunctions = moduleLoader.loadFunctions(builtinFunctions, DEFAULT_SCRIPT);
    return builtinFunctions.addAll(definedFunctions);
  }

  public Maybe<Set<Name>> parseArguments(List<String> args) {
    DuplicatesDetector<Name> duplicatesDetector = new DuplicatesDetector<>();
    for (String argument : args) {
      if (isLegalName(argument)) {
        duplicatesDetector.addValue(name(argument));
      } else {
        return Maybe.error("error: Illegal function name '" + argument
            + "' passed in command line.");
      }
    }

    for (Name name : duplicatesDetector.getDuplicateValues()) {
      return Maybe.error("error: Function " + name + " has been specified more than once.");
    }
    Set<Name> result = duplicatesDetector.getUniqueValues();
    if (result.isEmpty()) {
      return Maybe.error("error: Specify at least one function to be executed.");
    }
    return Maybe.element(result);
  }
}
