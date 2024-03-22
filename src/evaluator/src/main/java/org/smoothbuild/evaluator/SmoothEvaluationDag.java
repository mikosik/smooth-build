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
import org.smoothbuild.compilerfrontend.FindValues;
import org.smoothbuild.compilerfrontend.lang.define.ExprS;
import org.smoothbuild.compilerfrontend.lang.define.ModuleS;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;

public class SmoothEvaluationDag {
  public static Dag<EvaluatedExprs> smoothEvaluationDag(
      List<FullPath> modules, List<String> names) {
    Dag<ModuleS> moduleS = frontendCompilationDag(modules);
    return evaluate(apply2(InflateDag1.class, moduleS, value(names)));
  }

  public static class InflateDag1
      implements TryFunction2<ModuleS, List<String>, Dag<EvaluatedExprs>> {
    @Override
    public Label label() {
      return Label.label(COMPILE_PREFIX, "inflateEvaluationDag1");
    }

    @Override
    public Try<Dag<EvaluatedExprs>> apply(ModuleS moduleS, List<String> valueNames) {
      var scopeS = value(moduleS.membersAndImported());
      var valuesS = apply2(FindValues.class, scopeS, value(valueNames));
      var evaluationDag = evaluate(apply2(InflateDag2.class, scopeS, valuesS));
      return success(evaluationDag);
    }
  }

  public static class InflateDag2
      implements TryFunction2<ScopeS, List<ExprS>, Dag<EvaluatedExprs>> {
    @Override
    public Label label() {
      return Label.label(COMPILE_PREFIX, "inflateEvaluationDag2");
    }

    @Override
    public Try<Dag<EvaluatedExprs>> apply(ScopeS scopeS, List<ExprS> values) {
      var valuesS = value(values);
      var compiledToBytecode = apply2(BackendCompile.class, valuesS, value(scopeS.evaluables()));
      var evaluatedExprs = applyMaybeFunction(EvaluatorBFacade.class, compiledToBytecode);
      return success(evaluatedExprs);
    }
  }
}
