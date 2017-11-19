package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.parse.ModuleLoader.loadModule;
import static org.smoothbuild.util.Maybe.invoke;
import static org.smoothbuild.util.Maybe.invokeWrap;

import javax.inject.Inject;

import org.smoothbuild.SmoothPaths;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.util.Maybe;

public class List implements Command {
  private final SmoothPaths paths;
  private final Console console;

  @Inject
  public List(SmoothPaths paths, Console console) {
    this.paths = paths;
    this.console = console;
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
    Maybe<Functions> convert = loadModule(new Functions(), paths.convertModule());
    Maybe<Functions> funcs = loadModule(new Functions(), paths.funcsModule());
    Maybe<Functions> builtin1 = invokeWrap(convert, funcs, (c, f) -> c.addAll(f));
    Maybe<Functions> builtin = builtin1;
    return invoke(builtin, b -> loadModule(b, paths.defaultScript()));
  }
}
