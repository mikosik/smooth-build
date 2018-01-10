package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.util.Maybe.invoke;

import javax.inject.Inject;

import org.smoothbuild.SmoothPaths;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.parse.ModuleLoader;
import org.smoothbuild.util.Maybe;

public class List implements Command {
  private final SmoothPaths paths;
  private final Console console;
  private final ModuleLoader moduleLoader;

  @Inject
  public List(SmoothPaths paths, Console console, ModuleLoader moduleLoader) {
    this.paths = paths;
    this.console = console;
    this.moduleLoader = moduleLoader;
  }

  @Override
  public int run(String... names) {
    Maybe<Functions> functions = userDefinedFunctions();
    if (functions.hasValue()) {
      functions
          .value()
          .nameToFunctionMap()
          .values()
          .stream()
          .filter(f -> f.parameters().size() == 0)
          .map(t -> t.name().toString())
          .sorted()
          .forEach(n -> System.out.println(n));
    } else {
      for (Object error : functions.errors()) {
        console.rawError(error);
      }
    }
    console.printFinalSummary();
    return console.isErrorReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }

  private Maybe<Functions> userDefinedFunctions() {
    Maybe<Functions> builtin = moduleLoader.loadModule(new Functions(), paths.funcsModule());
    return invoke(builtin, b -> moduleLoader.loadModule(b, paths.defaultScript()));
  }
}
