package org.smoothbuild.run;

import static org.smoothbuild.lang.base.Space.USER;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.lang.base.Declared;
import org.smoothbuild.lang.base.Value;

public class ListRunner {
  private final Console console;
  private final RuntimeController runtimeController;

  @Inject
  public ListRunner(Console console, RuntimeController runtimeController) {
    this.console = console;
    this.runtimeController = runtimeController;
  }

  public int run() {
    return runtimeController.setUpRuntimeAndRun(defintions -> defintions
        .values()
        .values()
        .stream()
        .filter(f -> f.location().module().space().equals(USER))
        .filter(Value.class::isInstance)
        .map(Declared::name)
        .sorted()
        .forEach(console::println));
  }
}
