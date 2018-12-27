package org.smoothbuild.cli;

import javax.inject.Inject;

import org.smoothbuild.SmoothPaths;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.parse.RuntimeController;

public class List implements Command {
  private final SmoothPaths paths;
  private final Console console;
  private final RuntimeController runtimeController;

  @Inject
  public List(SmoothPaths paths, Console console, RuntimeController runtimeController) {
    this.paths = paths;
    this.console = console;
    this.runtimeController = runtimeController;
  }

  @Override
  public int run(String... args) {
    return runtimeController.setUpRuntimeAndRun(runtime -> {
      runtime
          .functions()
          .all()
          .stream()
          .filter(f -> f.location().file().equals(paths.defaultScript()))
          .filter(f -> f.parameters().size() == 0)
          .map(Function::name)
          .sorted()
          .forEach(console::println);
    });
  }
}
