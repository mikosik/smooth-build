package org.smoothbuild.run;

import static org.smoothbuild.io.fs.space.Space.PRJ;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.lang.base.define.DefinedS;
import org.smoothbuild.lang.base.define.ValS;

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
        .topEvals()
        .stream()
        .filter(f -> f.loc().file().space().equals(PRJ))
        .filter(ValS.class::isInstance)
        .map(DefinedS::name)
        .sorted()
        .forEach(console::println));
  }
}
