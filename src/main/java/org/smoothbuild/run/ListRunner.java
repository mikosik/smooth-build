package org.smoothbuild.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.fs.space.Space.PRJ;

import java.util.Optional;

import javax.inject.Inject;

import org.smoothbuild.lang.base.Nal;
import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.PolyRefableS;
import org.smoothbuild.lang.define.PolyValS;
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
          .refables()
          .asMap()
          .values()
          .stream()
          .filter(ListRunner::isEvaluableValue)
          .map(Nal::name)
          .sorted()
          .forEach(console::println);
      return EXIT_CODE_SUCCESS;
    } else {
      return EXIT_CODE_ERROR;
    }
  }

  private static boolean isEvaluableValue(PolyRefableS polyRefableS) {
    return polyRefableS.loc().file().space().equals(PRJ)
        && polyRefableS instanceof PolyValS
        && polyRefableS.schema().quantifiedVars().isEmpty();
  }
}
