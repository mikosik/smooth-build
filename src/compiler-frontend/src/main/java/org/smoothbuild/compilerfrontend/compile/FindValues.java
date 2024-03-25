package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.log.base.Try.failure;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_PREFIX;
import static org.smoothbuild.compilerfrontend.lang.base.location.Locations.commandLineLocation;

import java.util.ArrayList;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.dag.TryFunction2;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SInstantiate;
import org.smoothbuild.compilerfrontend.lang.define.SNamedValue;
import org.smoothbuild.compilerfrontend.lang.define.SReference;
import org.smoothbuild.compilerfrontend.lang.define.SScope;

public class FindValues implements TryFunction2<SScope, List<String>, List<SExpr>> {
  @Override
  public Label label() {
    return Label.label(COMPILE_PREFIX, "findValues");
  }

  @Override
  public Try<List<SExpr>> apply(SScope environment, List<String> valueNames) {
    var logger = new Logger();
    var namedEvaluables = new ArrayList<SNamedValue>();
    var evaluables = environment.evaluables();
    for (var name : valueNames) {
      var topEvaluable = evaluables.getMaybe(name);
      if (topEvaluable.isNone()) {
        logger.error("Unknown value `" + name + "`.\n"
            + "Try 'smooth list' to see all available values that can be calculated.");
      } else if (!(topEvaluable.get() instanceof SNamedValue namedValue)) {
        logger.error("`" + name + "` cannot be calculated as it is not a value but a function.");
      } else if (namedValue.schema().quantifiedVars().isEmpty()) {
        namedEvaluables.add(namedValue);
      } else {
        logger.error("`" + name + "` cannot be calculated as it is a polymorphic value.");
      }
    }
    if (logger.containsFailure()) {
      return failure(logger);
    }
    List<SExpr> exprs = listOfAll(namedEvaluables)
        .map(v -> new SInstantiate(referenceTo(v), commandLineLocation()));
    return Try.of(exprs, logger);
  }

  private static SReference referenceTo(SNamedValue sNamedValue) {
    return new SReference(sNamedValue.schema(), sNamedValue.name(), commandLineLocation());
  }
}