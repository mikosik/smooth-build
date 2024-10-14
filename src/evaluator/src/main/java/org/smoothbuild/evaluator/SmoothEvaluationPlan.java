package org.smoothbuild.evaluator;

import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.common.plan.Plan.apply2;
import static org.smoothbuild.common.plan.Plan.applyMaybeFunction;
import static org.smoothbuild.common.plan.Plan.evaluate;
import static org.smoothbuild.common.plan.Plan.value;
import static org.smoothbuild.evaluator.EvaluatorConstants.EVALUATE_LABEL;

import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.plan.Plan;
import org.smoothbuild.common.plan.TryFunction2;
import org.smoothbuild.compilerbackend.BackendCompile;
import org.smoothbuild.compilerbackend.CompiledExprs;
import org.smoothbuild.compilerfrontend.FrontendCompile;
import org.smoothbuild.compilerfrontend.lang.define.SModule;

public class SmoothEvaluationPlan {
  public static Plan<EvaluatedExprs> smoothEvaluationPlan(
      List<FullPath> modules, List<String> names) {
    Plan<CompiledExprs> compilationPlan = fullCompilationPlan(modules, names);
    return applyMaybeFunction(BEvaluatorFacade.class, compilationPlan);
  }

  private static Plan<CompiledExprs> fullCompilationPlan(
      List<FullPath> modules, List<String> names) {
    Plan<SModule> moduleS = Plan.task1(FrontendCompile.class, promise(modules));
    return evaluate(apply2(InflatePlan.class, moduleS, value(names)));
  }

  public static class InflatePlan
      implements TryFunction2<SModule, List<String>, Plan<CompiledExprs>> {
    @Override
    public Label label() {
      return EVALUATE_LABEL.append("scheduleBackendCompile");
    }

    @Override
    public Try<Plan<CompiledExprs>> apply(SModule sModule, List<String> valueNames) {
      var scopeS = sModule.membersAndImported();
      var evaluables = value(scopeS.evaluables());

      var findingPlan = Plan.task2(FindValues.class, promise(scopeS), promise(valueNames));
      var backendCompilationPlan = apply2(BackendCompile.class, findingPlan, evaluables);
      return success(backendCompilationPlan);
    }
  }
}
