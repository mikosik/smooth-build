package org.smoothbuild.run;

import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.compile.frontend.lang.base.location.Locations.commandLineLocation;
import static org.smoothbuild.out.log.Try.failure;

import java.util.ArrayList;
import java.util.function.Function;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.compile.frontend.lang.define.ExprS;
import org.smoothbuild.compile.frontend.lang.define.InstantiateS;
import org.smoothbuild.compile.frontend.lang.define.NamedValueS;
import org.smoothbuild.compile.frontend.lang.define.ReferenceS;
import org.smoothbuild.compile.frontend.lang.define.ScopeS;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.out.log.Try;

public class FindValues implements Function<Tuple2<ScopeS, List<String>>, Try<List<ExprS>>> {

  @Override
  public Try<List<ExprS>> apply(Tuple2<ScopeS, List<String>> argument) {
    var logger = new Logger();
    var valueNames = argument.element2();
    var environment = argument.element1();
    var namedEvaluables = new ArrayList<NamedValueS>();
    var evaluables = environment.evaluables();
    for (var name : valueNames) {
      var topEvaluable = evaluables.getMaybe(name);
      if (topEvaluable.isNone()) {
        logger.error("Unknown value `" + name + "`.\n"
            + "Try 'smooth list' to see all available values that can be calculated.");
      } else if (!(topEvaluable.get() instanceof NamedValueS namedValue)) {
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
    List<ExprS> exprs = listOfAll(namedEvaluables)
        .map(v -> new InstantiateS(referenceTo(v), commandLineLocation()));
    return Try.of(exprs, logger);
  }

  private static ReferenceS referenceTo(NamedValueS namedValueS) {
    return new ReferenceS(namedValueS.schema(), namedValueS.name(), commandLineLocation());
  }
}
