package org.smoothbuild.run;

import static org.smoothbuild.lang.base.define.Space.USER;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.lang.base.define.Defined;
import org.smoothbuild.lang.base.define.Value;

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
        .filter(f -> f.location().file().space().equals(USER))
        .filter(Value.class::isInstance)
        .map(Defined::name)
        .sorted()
        .forEach(console::println));
  }
}
