package org.smoothbuild.exec.run;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.install.InstallationPaths;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.parse.RuntimeController;

public class ListRunner {
  private final InstallationPaths paths;
  private final Console console;
  private final RuntimeController runtimeController;

  @Inject
  public ListRunner(InstallationPaths paths, Console console, RuntimeController runtimeController) {
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
