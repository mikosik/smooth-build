package org.smoothbuild.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;

import javax.inject.Inject;

import org.smoothbuild.compile.fs.lang.base.Nal;
import org.smoothbuild.compile.fs.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.fs.lang.define.NamedValueS;
import org.smoothbuild.out.report.Console;
import org.smoothbuild.out.report.Reporter;

public class ListRunner {
  private final Reporter reporter;
  private final Console console;
  private final DefinitionsLoader definitionsLoader;

  @Inject
  public ListRunner(DefinitionsLoader definitionsLoader, Reporter reporter, Console console) {
    this.reporter = reporter;
    this.console = console;
    this.definitionsLoader = definitionsLoader;
  }

  public int run() {
    var definitions = definitionsLoader.loadDefinitions();
    if (definitions.isPresent()) {
      reporter.startNewPhase("Values that can be evaluated:");
      definitions.get()
          .evaluables()
          .asMap()
          .values()
          .stream()
          .filter(ListRunner::isEvaluableValue)
          .map(Nal::name)
          .sorted()
          .forEach(line -> console.println("  " + line));
      return EXIT_CODE_SUCCESS;
    } else {
      return EXIT_CODE_ERROR;
    }
  }

  private static boolean isEvaluableValue(NamedEvaluableS evaluable) {
    return evaluable.location().isInProjectSpace()
        && evaluable instanceof NamedValueS
        && evaluable.schema().quantifiedVars().isEmpty();
  }
}
