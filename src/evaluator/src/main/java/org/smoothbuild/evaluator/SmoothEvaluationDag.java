package org.smoothbuild.evaluator;

import static org.smoothbuild.common.dag.Dag.apply2;
import static org.smoothbuild.common.dag.Dag.applyMaybeFunction;
import static org.smoothbuild.common.dag.Dag.evaluate;
import static org.smoothbuild.common.dag.Dag.value;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_PREFIX;
import static org.smoothbuild.compilerfrontend.ModuleFrontendCompilationDag.frontendCompilationDag;

import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.dag.Dag;
import org.smoothbuild.common.dag.TryFunction2;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.compilerbackend.BackendCompile;
import org.smoothbuild.compilerbackend.CompiledExprs;
import org.smoothbuild.compilerfrontend.compile.FindValues;
import org.smoothbuild.compilerfrontend.lang.define.ModuleS;

public class SmoothEvaluationDag {
  public static Dag<EvaluatedExprs> smoothEvaluationDag(
      List<FullPath> modules, List<String> names) {
    Dag<CompiledExprs> compiledExprs = frontBackCompilationDag(modules, names);
    return applyMaybeFunction(BEvaluatorFacade.class, compiledExprs);
  }

  private static Dag<CompiledExprs> frontBackCompilationDag(
      List<FullPath> modules, List<String> names) {
    Dag<ModuleS> moduleS = frontendCompilationDag(modules);
    return evaluate(apply2(InflateDag1.class, moduleS, value(names)));
  }

  public static class InflateDag1
      implements TryFunction2<ModuleS, List<String>, Dag<CompiledExprs>> {
    @Override
    public Label label() {
      return Label.label(COMPILE_PREFIX, "inflateBackendCompilationDag");
    }

    @Override
    public Try<Dag<CompiledExprs>> apply(ModuleS moduleS, List<String> valueNames) {
      var scopeS = value(moduleS.membersAndImported());
      var evaluables = value(moduleS.membersAndImported().evaluables());

      var valuesS = apply2(FindValues.class, scopeS, value(valueNames));
      var compiledExprs = apply2(BackendCompile.class, valuesS, evaluables);
      return success(compiledExprs);
    }
  }
}
