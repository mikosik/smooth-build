package org.smoothbuild.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.io.fs.space.Space.PRJ;

import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.lang.base.define.DefinedS;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.lang.base.define.ValS;
import org.smoothbuild.out.console.Console;
import org.smoothbuild.out.report.Reporter;

public class ListRunner {
  private final Reporter reporter;
  private final Console console;
  private final DefsLoader defsLoader;

  @Inject
  public ListRunner(DefsLoader defsLoader, Reporter reporter, Console console) {
    this.reporter = reporter;
    this.console = console;
    this.defsLoader = defsLoader;
  }

  public int run() {
    Optional<DefsS> defsS = defsLoader.loadDefs();
    if (defsS.isPresent()) {
      reporter.startNewPhase("Values that can be evaluated:");
      defsS.get()
          .topEvals()
          .stream()
          .filter(f -> f.loc().file().space().equals(PRJ))
          .filter(ValS.class::isInstance)
          .map(DefinedS::name)
          .sorted()
          .forEach(console::println);
      return EXIT_CODE_SUCCESS;
    } else {
      return EXIT_CODE_ERROR;
    }
  }
}
