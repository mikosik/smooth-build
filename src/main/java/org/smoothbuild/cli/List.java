package org.smoothbuild.cli;

import javax.inject.Inject;

import org.smoothbuild.SmoothPaths;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.parse.RuntimeController;

public class List {
  private final SmoothPaths paths;
  private final Console console;
  private final RuntimeController runtimeController;

  @Inject
  public List(SmoothPaths paths, Console console, RuntimeController runtimeController) {
    this.paths = paths;
    this.console = console;
    this.runtimeController = runtimeController;
  }

  public int run() {
    return runtimeController.setUpRuntimeAndRun(runtime -> runtime
        .functions()
        .all()
        .stream()
        .filter(f -> f.location().path().equals(paths.userModule()))
        .filter(f -> f.parameters().size() == 0)
        .map(Function::name)
        .sorted()
        .forEach(console::println));
  }
}
