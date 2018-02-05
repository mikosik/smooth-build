package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;

import javax.inject.Inject;

import org.smoothbuild.SmoothPaths;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.parse.RuntimeLoader;
import org.smoothbuild.util.Maybe;

public class List implements Command {
  private final SmoothPaths paths;
  private final Console console;
  private final RuntimeLoader runtimeLoader;

  @Inject
  public List(SmoothPaths paths, Console console, RuntimeLoader runtimeLoader) {
    this.paths = paths;
    this.console = console;
    this.runtimeLoader = runtimeLoader;
  }

  @Override
  public int run(String... names) {
    Maybe<Functions> functions = runtimeLoader.loadFunctions();
    if (functions.hasValue()) {
      functions
          .value()
          .nameToFunctionMap()
          .values()
          .stream()
          .filter(f -> f.location().file().equals(paths.defaultScript()))
          .filter(f -> f.parameters().size() == 0)
          .map(t -> t.name().toString())
          .sorted()
          .forEach(n -> System.out.println(n));
    } else {
      console.rawErrors(functions.errors());
    }
    console.printFinalSummary();
    return console.isErrorReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }
}
