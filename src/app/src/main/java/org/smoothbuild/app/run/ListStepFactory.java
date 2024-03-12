package org.smoothbuild.app.run;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.app.layout.SmoothSpace.PROJECT;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.common.step.Step.tryStep;
import static org.smoothbuild.compilerfrontend.FrontendCompilerStep.createFrontendCompilerStep;

import org.smoothbuild.app.layout.Layout;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.step.Step;
import org.smoothbuild.common.step.StepFactory;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.compilerfrontend.lang.base.Nal;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.location.SourceLocation;
import org.smoothbuild.compilerfrontend.lang.define.NamedEvaluableS;
import org.smoothbuild.compilerfrontend.lang.define.NamedValueS;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;

public class ListStepFactory implements StepFactory<Tuple0, String> {
  @Override
  public Step<Tuple0, String> create(Tuple0 v) {
    return createFrontendCompilerStep(Layout.MODULES)
        .then(tryStep(ListStepFactory::printEvaluables));
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
    return isInProjectSpace(evaluable.location())
        && evaluable instanceof NamedValueS
        && evaluable.schema().quantifiedVars().isEmpty();
  }

  private static boolean isInProjectSpace(Location location) {
    return (location instanceof SourceLocation source) && PROJECT.equals(source.space());
  }
}
