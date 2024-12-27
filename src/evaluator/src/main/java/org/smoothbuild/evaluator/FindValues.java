package org.smoothbuild.evaluator;

import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.log.location.Locations.commandLineLocation;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.evaluator.EvaluatorConstants.EVALUATOR_LABEL;

import java.util.ArrayList;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SInstantiate;
import org.smoothbuild.compilerfrontend.lang.define.SNamedValue;
import org.smoothbuild.compilerfrontend.lang.define.SReference;
import org.smoothbuild.compilerfrontend.lang.define.SScope;

public class FindValues implements Task2<SScope, List<String>, List<SExpr>> {
  @Override
  public Output<List<SExpr>> execute(SScope environment, List<String> valueNames) {
    var logger = new Logger();
    var result = new ArrayList<SNamedValue>();
    var namedEvaluables = environment.evaluables();
    for (var name : valueNames) {
      var namedEvaluable = namedEvaluables.getMaybe(name);
      if (namedEvaluable.isNone()) {
        logger.error("Unknown value `" + name + "`.\n"
            + "Try 'smooth list' to see all available values that can be calculated.");
      } else if (!(namedEvaluable.get() instanceof SNamedValue namedValue)) {
        logger.error("`" + name + "` cannot be calculated as it is not a value but a function.");
      } else if (namedValue.schema().quantifiedVars().isEmpty()) {
        result.add(namedValue);
      } else {
        logger.error("`" + name + "` cannot be calculated as it is a polymorphic value.");
      }
    }
    var label = EVALUATOR_LABEL.append(":findValues");
    if (logger.containsFailure()) {
      return output(label, logger.toList());
    }
    List<SExpr> exprs =
        listOfAll(result).map(v -> new SInstantiate(referenceTo(v), commandLineLocation()));
    return output(exprs, label, logger.toList());
  }

  private static SReference referenceTo(SNamedValue sNamedValue) {
    return new SReference(sNamedValue.schema(), sNamedValue.id(), commandLineLocation());
  }
}
