package org.smoothbuild.exec.run;

import static org.smoothbuild.lang.base.Space.USER;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.parse.RuntimeController;

public class ListRunner {
  private final Console console;
  private final RuntimeController runtimeController;

  @Inject
  public ListRunner(Console console, RuntimeController runtimeController) {
    this.console = console;
    this.runtimeController = runtimeController;
  }

  public int run() {
    return runtimeController.setUpRuntimeAndRun(runtime -> runtime
        .functions()
        .all()
        .stream()
        .filter(f -> f.location().module().space().equals(USER))
        .filter(f -> f.parameters().size() == 0)
        .map(Function::name)
        .sorted()
        .forEach(console::println));
  }
}
