package org.smoothbuild.evaluator;

import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Result.error;
import static org.smoothbuild.common.collect.Result.ok;
import static org.smoothbuild.common.log.location.Locations.commandLineLocation;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.parseReference;
import static org.smoothbuild.evaluator.EvaluatorConstants.EVALUATOR_LABEL;

import java.util.ArrayList;
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SInstantiate;
import org.smoothbuild.compilerfrontend.lang.define.SNamedValue;
import org.smoothbuild.compilerfrontend.lang.define.SReference;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public class FindValues implements Task2<SScope, List<String>, List<SExpr>> {
  @Override
  public Output<List<SExpr>> execute(SScope environment, List<String> valueNames) {
    var logger = new Logger();
    var result = new ArrayList<SNamedValue>();
    for (var name : valueNames) {
      parseFqn(name)
          .flatMapRight(fqn -> getNamedEvaluable(environment, fqn))
          .ifRight(result::add)
          .ifLeft(logger::error);
    }
    var label = EVALUATOR_LABEL.append(":findValues");
    if (logger.containsFailure()) {
      return output(label, logger.toList());
    }
    List<SExpr> exprs =
        listOfAll(result).map(v -> new SInstantiate(referenceTo(v), commandLineLocation()));
    return output(exprs, label, logger.toList());
  }

  private static Either<String, Fqn> parseFqn(String name) {
    return parseReference(name).mapLeft(message -> "Illegal reference `" + name + "`. " + message);
  }

  private static Either<String, SNamedValue> getNamedEvaluable(SScope environment, Fqn fqn) {
    return environment
        .evaluables()
        .find(fqn)
        .mapLeft(e -> unknownFqnMessage(fqn))
        .flatMapRight(e -> {
          if (e instanceof SNamedValue namedValue) {
            if (namedValue.schema().quantifiedVars().isEmpty()) {
              return ok(namedValue);
            } else {
              return error(e.id().q() + " cannot be calculated as it is a polymorphic value.");
            }
          } else {
            return error(e.id().q() + " cannot be calculated as it is not a value but a function.");
          }
        });
  }

  private static String unknownFqnMessage(Fqn fqn) {
    return "Unknown value " + fqn.q() + ".\n"
        + "Try 'smooth list' to see all available values that can be calculated.";
  }

  private static SReference referenceTo(SNamedValue sNamedValue) {
    return new SReference(sNamedValue.schema(), sNamedValue.id(), commandLineLocation());
  }
}
