package org.smoothbuild.evaluator;

import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Result.err;
import static org.smoothbuild.common.collect.Result.ok;
import static org.smoothbuild.common.log.location.Locations.commandLineLocation;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.parseReference;
import static org.smoothbuild.evaluator.EvaluatorConstants.EVALUATOR_LABEL;

import java.util.ArrayList;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Result;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SInstantiate;
import org.smoothbuild.compilerfrontend.lang.define.SNamedValue;
import org.smoothbuild.compilerfrontend.lang.define.SPolyReference;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public class FindValues implements Task2<SScope, List<String>, List<SExpr>> {
  @Override
  public Output<List<SExpr>> execute(SScope environment, List<String> valueNames) {
    var logger = new Logger();
    var result = new ArrayList<SNamedValue>();
    for (var name : valueNames) {
      parseFqn(name)
          .flatMapOk(fqn -> getNamedEvaluable(environment, fqn))
          .ifOk(result::add)
          .ifErr(logger::error);
    }
    var label = EVALUATOR_LABEL.append(":findValues");
    if (logger.containsFailure()) {
      return output(label, logger.toList());
    }
    List<SExpr> exprs =
        listOfAll(result).map(v -> new SInstantiate(referenceTo(v), commandLineLocation()));
    return output(exprs, label, logger.toList());
  }

  private static Result<Fqn> parseFqn(String name) {
    return parseReference(name).mapErr(message -> "Illegal reference `" + name + "`. " + message);
  }

  private static Result<SNamedValue> getNamedEvaluable(SScope environment, Fqn fqn) {
    return environment
        .evaluables()
        .find(fqn)
        .mapErr(e -> unknownFqnMessage(fqn))
        .flatMapOk(e -> {
          if (e instanceof SNamedValue namedValue) {
            if (namedValue.typeScheme().typeParams().isEmpty()) {
              return ok(namedValue);
            } else {
              return err(e.fqn().q() + " cannot be calculated as it is a polymorphic value.");
            }
          } else {
            return err(e.fqn().q() + " cannot be calculated as it is not a value but a function.");
          }
        });
  }

  private static String unknownFqnMessage(Fqn fqn) {
    return "Unknown value " + fqn.q() + ".\n"
        + "Try 'smooth list' to see all available values that can be calculated.";
  }

  private static SPolyReference referenceTo(SNamedValue sNamedValue) {
    return new SPolyReference(sNamedValue.typeScheme(), sNamedValue.fqn(), commandLineLocation());
  }
}
