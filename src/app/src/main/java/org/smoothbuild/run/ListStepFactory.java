package org.smoothbuild.run;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.common.log.Try.success;
import static org.smoothbuild.common.step.Step.step;
import static org.smoothbuild.run.CreateFrontendCompilerStep.frontendCompilerStep;

import org.smoothbuild.common.log.Try;
import org.smoothbuild.common.step.Step;
import org.smoothbuild.common.step.StepFactory;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.compile.frontend.lang.base.Nal;
import org.smoothbuild.compile.frontend.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.frontend.lang.define.NamedValueS;
import org.smoothbuild.compile.frontend.lang.define.ScopeS;

public class ListStepFactory implements StepFactory<Tuple0, String> {
  @Override
  public Step<Tuple0, String> create(Tuple0 v) {
    return frontendCompilerStep().then(step(ListStepFactory::printEvaluables));
  }

  private static Try<String> printEvaluables(ScopeS scopeS) {
    var oneValuePerLineString = scopeS.evaluables().toMap().values().stream()
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
