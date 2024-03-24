package org.smoothbuild.app.run;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.app.layout.SmoothBucketId.PROJECT;
import static org.smoothbuild.common.dag.Dag.apply1;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.compilerfrontend.ModuleFrontendCompilationDag.frontendCompilationDag;

import org.smoothbuild.app.layout.Layout;
import org.smoothbuild.common.dag.Dag;
import org.smoothbuild.common.dag.TryFunction1;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.compilerfrontend.lang.base.Nal;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.location.SourceLocation;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.lang.define.SNamedValue;

public class ListEvaluablesDag {
  public static Dag<Void> listEvaluablesDag() {
    var scopeS = frontendCompilationDag(Layout.MODULES);
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
              .filter(ListEvaluablesDag::isNoArgNotGenericValue)
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
