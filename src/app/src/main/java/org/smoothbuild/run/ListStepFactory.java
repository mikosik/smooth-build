package org.smoothbuild.run;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.compile.frontend.FrontendCompilerStep.frontendCompilerStep;
import static org.smoothbuild.out.log.Maybe.success;
import static org.smoothbuild.run.step.Step.step;

import org.smoothbuild.compile.frontend.lang.base.Nal;
import org.smoothbuild.compile.frontend.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.frontend.lang.define.NamedValueS;
import org.smoothbuild.compile.frontend.lang.define.ScopeS;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.run.step.Step;
import org.smoothbuild.run.step.StepFactory;

import io.vavr.Tuple0;

public class ListStepFactory implements StepFactory<Tuple0, String> {
  @Override
  public Step<Tuple0, String> create(Tuple0 v) {
    return frontendCompilerStep()
        .then(step(ListStepFactory::printEvaluables));
  }

  private static Maybe<String> printEvaluables(ScopeS scopeS) {
    var oneValuePerLineString = scopeS
        .evaluables()
        .toMap()
        .values()
        .stream()
        .filter(ListStepFactory::isNoArgNotGenericValue)
        .map(Nal::name)
        .sorted()
        .collect(joining("\n"));
    return success("Values that can be evaluated:\n" + oneValuePerLineString);
  }

  private static boolean isNoArgNotGenericValue(NamedEvaluableS evaluable) {
    return evaluable.location().isInProjectSpace()
        && evaluable instanceof NamedValueS
        && evaluable.schema().quantifiedVars().isEmpty();
  }
}
