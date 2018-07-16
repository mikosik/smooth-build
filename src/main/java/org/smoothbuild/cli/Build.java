package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.ARTIFACTS_PATH;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.SmoothConstants.TEMPORARY_PATH;
import static org.smoothbuild.lang.base.Name.isLegalName;
import static org.smoothbuild.util.Maybe.error;
import static org.smoothbuild.util.Maybe.value;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.parse.RuntimeLoader;
import org.smoothbuild.task.exec.SmoothExecutor;
import org.smoothbuild.util.DuplicatesDetector;
import org.smoothbuild.util.Maybe;

import com.google.common.collect.ImmutableList;

public class Build implements Command {
  private final Console console;
  private final RuntimeLoader runtimeLoader;
  private final SmoothExecutor smoothExecutor;
  private final FileSystem fileSystem;

  @Inject
  public Build(Console console, RuntimeLoader runtimeLoader, SmoothExecutor smoothExecutor,
      FileSystem fileSystem) {
    this.console = console;
    this.runtimeLoader = runtimeLoader;
    this.smoothExecutor = smoothExecutor;
    this.fileSystem = fileSystem;
  }

  @Override
  public int run(String... args) {
    List<String> argsWithoutFirst = ImmutableList.copyOf(args).subList(1, args.length);
    Maybe<Set<String>> functionNames = parseArguments(argsWithoutFirst);
    if (!functionNames.hasValue()) {
      console.rawErrors(functionNames.errors());
      return EXIT_CODE_ERROR;
    }
    fileSystem.delete(ARTIFACTS_PATH);
    fileSystem.delete(TEMPORARY_PATH);
    List<? extends Object> errors = runtimeLoader.load();
    if (errors.isEmpty()) {
      smoothExecutor.execute(functionNames.value());
    } else {
      console.rawErrors(errors);
    }
    console.printFinalSummary();
    return console.isErrorReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }

  public Maybe<Set<String>> parseArguments(List<String> args) {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    for (String argument : args) {
      if (isLegalName(argument)) {
        duplicatesDetector.addValue(argument);
      } else {
        return error("error: Illegal function name '" + argument
            + "' passed in command line.");
      }
    }

    for (String name : duplicatesDetector.getDuplicateValues()) {
      return error("error: Function '" + name + "' has been specified more than once.");
    }
    Set<String> result = duplicatesDetector.getUniqueValues();
    if (result.isEmpty()) {
      return error("error: Specify at least one function to be executed.\n"
          + "Use 'smooth list' to see all available functions.");

    }
    return value(result);
  }
}
