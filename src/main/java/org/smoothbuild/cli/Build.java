package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.DEFAULT_SCRIPT;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.lang.function.base.Name.isLegalName;
import static org.smoothbuild.lang.function.nativ.NativeLibraryLoader.loadBuiltinFunctions;
import static org.smoothbuild.parse.ModuleLoader.loadModule;
import static org.smoothbuild.util.Maybe.error;
import static org.smoothbuild.util.Maybe.invokeWrap;
import static org.smoothbuild.util.Maybe.value;

import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.task.exec.ExecutionException;
import org.smoothbuild.task.exec.SmoothExecutor;
import org.smoothbuild.util.DuplicatesDetector;
import org.smoothbuild.util.Maybe;

import com.google.common.collect.ImmutableList;

public class Build {
  private final Console console;
  private final TempManager tempManager;
  private final SmoothExecutor smoothExecutor;

  @Inject
  public Build(Console console, TempManager tempManager, SmoothExecutor smoothExecutor) {
    this.console = console;
    this.tempManager = tempManager;
    this.smoothExecutor = smoothExecutor;
  }

  public int run(String... names) {
    List<String> argsWithoutFirst = ImmutableList.copyOf(names).subList(1, names.length);
    Maybe<Set<Name>> functionNames = parseArguments(argsWithoutFirst);
    if (!functionNames.hasValue()) {
      for (Object error : functionNames.errors()) {
        console.rawError(error);
      }
      return EXIT_CODE_ERROR;
    }
    tempManager.removeTemps();
    try {
      Maybe<Functions> functions = loadFunctions();
      if (functions.hasValue()) {
        smoothExecutor.execute(functions.value(), functionNames.value());
      } else {
        for (Object error : functions.errors()) {
          console.rawError(error);
        }
      }
    } catch (ExecutionException e) {
      return EXIT_CODE_ERROR;
    }

    console.printFinalSummary();
    return console.isErrorReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }

  private Maybe<Functions> loadFunctions() {
    Functions builtin = loadBuiltinFunctions();
    Maybe<Functions> defined = loadModule(builtin, Paths.get(DEFAULT_SCRIPT.value()));
    return invokeWrap(defined, builtin::addAll);
  }

  public Maybe<Set<Name>> parseArguments(List<String> args) {
    DuplicatesDetector<Name> duplicatesDetector = new DuplicatesDetector<>();
    for (String argument : args) {
      if (isLegalName(argument)) {
        duplicatesDetector.addValue(new Name(argument));
      } else {
        return error("error: Illegal function name '" + argument
            + "' passed in command line.");
      }
    }

    for (Name name : duplicatesDetector.getDuplicateValues()) {
      return error("error: Function '" + name + "' has been specified more than once.");
    }
    Set<Name> result = duplicatesDetector.getUniqueValues();
    if (result.isEmpty()) {
      return error("error: Specify at least one function to be executed.");
    }
    return value(result);
  }
}
