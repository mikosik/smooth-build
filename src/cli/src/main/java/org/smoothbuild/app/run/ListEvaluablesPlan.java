package org.smoothbuild.app.run;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.app.layout.BucketIds.PROJECT;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.common.plan.Plan.apply1;
import static org.smoothbuild.compilerfrontend.FrontendCompilationPlan.frontendCompilationPlan;

import org.smoothbuild.app.layout.Layout;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.plan.Plan;
import org.smoothbuild.common.plan.TryFunction1;
import org.smoothbuild.compilerfrontend.lang.base.Nal;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.location.SourceLocation;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.lang.define.SNamedValue;

public class ListEvaluablesPlan {
  public static Plan<Void> listEvaluablesPlan() {
    var scopeS = frontendCompilationPlan(Layout.MODULES);
    return apply1(PrintEvaluables.class, scopeS);
  }

  public static class PrintEvaluables implements TryFunction1<SModule, Void> {
    @Override
    public Label label() {
      return Label.label("cli", "list");
    }

    @Override
    public Try<Void> apply(SModule sModule) {
      var oneValuePerLineString =
          sModule.membersAndImported().evaluables().toMap().values().stream()
              .filter(ListEvaluablesPlan::isNoArgNotGenericValue)
              .map(Nal::name)
              .sorted()
              .collect(joining("\n"));
      return success(null, info("Values that can be evaluated:\n" + oneValuePerLineString));
    }
  }

  private static boolean isNoArgNotGenericValue(SNamedEvaluable evaluable) {
    return isInProjectSpace(evaluable.location())
        && evaluable instanceof SNamedValue
        && evaluable.schema().quantifiedVars().isEmpty();
  }

  private static boolean isInProjectSpace(Location location) {
    return (location instanceof SourceLocation source) && PROJECT.equals(source.bucketId());
  }
}
