package org.smoothbuild.run;

import static org.smoothbuild.lang.base.Space.USER;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.lang.base.Defined;
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
        .referencables()
        .values()
        .stream()
        .filter(f -> f.location().module().space().equals(USER))
        .filter(Value.class::isInstance)
        .map(Defined::name)
        .sorted()
        .forEach(console::println));
  }
}
