package org.smoothbuild.app.run;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.app.layout.SmoothBucketId.PROJECT;
import static org.smoothbuild.common.dag.Dag.apply1;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.compilerfrontend.ModuleFrontendCompilationDag.frontendCompilationDag;

import org.smoothbuild.app.layout.Layout;
import org.smoothbuild.common.dag.Dag;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.compilerfrontend.lang.base.Nal;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.location.SourceLocation;
import org.smoothbuild.compilerfrontend.lang.define.ModuleS;
import org.smoothbuild.compilerfrontend.lang.define.NamedEvaluableS;
import org.smoothbuild.compilerfrontend.lang.define.NamedValueS;

public class ListEvaluablesDag {
  public static Dag<String> listEvaluablesDag() {
    var scopeS = frontendCompilationDag(Layout.MODULES);
    return apply1(ListEvaluablesDag::evaluablesToString, scopeS);
  }

  private static Try<String> evaluablesToString(ModuleS moduleS) {
    var oneValuePerLineString = moduleS.membersAndImported().evaluables().toMap().values().stream()
        .filter(ListEvaluablesDag::isNoArgNotGenericValue)
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
    return (location instanceof SourceLocation source) && PROJECT.equals(source.bucketId());
  }
}
