package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.ARTIFACTS_PATH;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.SmoothConstants.TEMPORARY_PATH;
import static org.smoothbuild.lang.function.base.Name.isLegalName;
import static org.smoothbuild.util.Maybe.error;
import static org.smoothbuild.util.Maybe.invoke;
import static org.smoothbuild.util.Maybe.invokeWrap;
import static org.smoothbuild.util.Maybe.value;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.SmoothPaths;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.parse.ModuleLoader;
import org.smoothbuild.task.exec.SmoothExecutor;
import org.smoothbuild.util.DuplicatesDetector;
import org.smoothbuild.util.Maybe;

import com.google.common.collect.ImmutableList;

public class Build implements Command {
  private final SmoothPaths paths;
  private final Console console;
  private final ModuleLoader moduleLoader;
  private final SmoothExecutor smoothExecutor;
  private final FileSystem fileSystem;

  @Inject
  public Build(SmoothPaths paths, Console console, ModuleLoader moduleLoader,
      SmoothExecutor smoothExecutor, FileSystem fileSystem) {
    this.paths = paths;
    this.console = console;
    this.moduleLoader = moduleLoader;
    this.smoothExecutor = smoothExecutor;
    this.fileSystem = fileSystem;
  }

  @Override
  public int run(String... names) {
    List<String> argsWithoutFirst = ImmutableList.copyOf(names).subList(1, names.length);
    Maybe<Set<Name>> functionNames = parseArguments(argsWithoutFirst);
    if (!functionNames.hasValue()) {
      for (Object error : functionNames.errors()) {
        console.rawError(error);
      }
      return EXIT_CODE_ERROR;
    }
    fileSystem.delete(ARTIFACTS_PATH);
    fileSystem.delete(TEMPORARY_PATH);
    Maybe<Functions> functions = loadFunctions();
    if (functions.hasValue()) {
      smoothExecutor.execute(functions.value(), functionNames.value());
    } else {
      for (Object error : functions.errors()) {
        console.rawError(error);
      }
    }
    console.printFinalSummary();
    return console.isErrorReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }

  private Maybe<Functions> loadFunctions() {
    Maybe<Functions> builtin = moduleLoader.loadModule(new Functions(), paths.funcsModule());
    Maybe<Functions> userFunctions = invoke(
        builtin, b -> moduleLoader.loadModule(b, paths.defaultScript()));
    return invokeWrap(userFunctions, builtin, (u, b) -> b.addAll(u));
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
      return error("error: Specify at least one function to be executed.\n"
          + "Use 'smooth list' to see all available functions.");

    }
    return value(result);
  }
}
