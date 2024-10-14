package org.smoothbuild.evaluator;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.plan.Plan.applyMaybeFunction;
import static org.smoothbuild.common.task.Output.schedulingOutput;
import static org.smoothbuild.evaluator.EvaluatorConstants.EVALUATE_LABEL;

import jakarta.inject.Inject;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.plan.Plan;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task2;
import org.smoothbuild.common.task.TaskExecutor;
import org.smoothbuild.compilerbackend.BackendCompile;
import org.smoothbuild.compilerbackend.CompiledExprs;
import org.smoothbuild.compilerfrontend.FrontendCompile;
import org.smoothbuild.compilerfrontend.lang.define.SModule;

public class SmoothEvaluationPlan {
  public static Plan<EvaluatedExprs> smoothEvaluationPlan(
      TaskExecutor taskExecutor, List<FullPath> modules, List<String> names) {
    var moduleS = taskExecutor.submit(FrontendCompile.class, promise(modules));
    Plan<CompiledExprs> compilationPlan =
        Plan.task2(ScheduleBackendCompile.class, moduleS, promise(names));
    return applyMaybeFunction(BEvaluatorFacade.class, compilationPlan);
  }

  public static class ScheduleBackendCompile
      implements Task2<CompiledExprs, SModule, List<String>> {
    private final TaskExecutor taskExecutor;

    @Inject
    public ScheduleBackendCompile(TaskExecutor taskExecutor) {
      this.taskExecutor = taskExecutor;
    }

    @Override
    public Output<CompiledExprs> execute(SModule sModule, List<String> valueNames) {
      var label = EVALUATE_LABEL.append("scheduleBackendCompile");
      var scopeS = sModule.membersAndImported();
      var evaluables = scopeS.evaluables();

      var values = taskExecutor.submit(FindValues.class, promise(scopeS), promise(valueNames));
      var compiledExprs = taskExecutor.submit(BackendCompile.class, values, promise(evaluables));
      return schedulingOutput(compiledExprs, report(label, new Trace(), EXECUTION, list()));
    }
  }
}
