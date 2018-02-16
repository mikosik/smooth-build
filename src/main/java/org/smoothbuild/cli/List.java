package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;

import javax.inject.Inject;

import org.smoothbuild.SmoothPaths;
import org.smoothbuild.lang.runtime.SRuntime;
import org.smoothbuild.parse.RuntimeLoader;

public class List implements Command {
  private final SRuntime runtime;
  private final SmoothPaths paths;
  private final Console console;
  private final RuntimeLoader runtimeLoader;

  @Inject
  public List(SRuntime runtime, SmoothPaths paths, Console console, RuntimeLoader runtimeLoader) {
    this.runtime = runtime;
    this.paths = paths;
    this.console = console;
    this.runtimeLoader = runtimeLoader;
  }

  @Override
  public int run(String... names) {
    java.util.List<? extends Object> errors = runtimeLoader.load();
    if (errors.isEmpty()) {
      runtime
          .functions()
          .functions()
          .stream()
          .filter(f -> f.location().file().equals(paths.defaultScript()))
          .filter(f -> f.parameters().size() == 0)
          .map(t -> t.name().toString())
          .sorted()
          .forEach(n -> System.out.println(n));
    } else {
      console.rawErrors(errors);
    }
    console.printFinalSummary();
    return console.isErrorReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }
}
