package org.smoothbuild.evaluator;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.task.Argument.argument;
import static org.smoothbuild.common.task.Output.schedulingOutput;
import static org.smoothbuild.evaluator.EvaluatorConstants.EVALUATE_LABEL;

import jakarta.inject.Inject;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task2;
import org.smoothbuild.common.task.TaskExecutor;
import org.smoothbuild.compilerbackend.BackendCompile;
import org.smoothbuild.compilerbackend.CompiledExprs;
import org.smoothbuild.compilerfrontend.FrontendCompile;
import org.smoothbuild.compilerfrontend.lang.define.SModule;

public class ScheduleEvaluate implements Task2<EvaluatedExprs, List<FullPath>, List<String>> {
  private final TaskExecutor taskExecutor;

  @Inject
  public ScheduleEvaluate(TaskExecutor taskExecutor) {
    this.taskExecutor = taskExecutor;
  }

  @Override
  public Output<EvaluatedExprs> execute(List<FullPath> modules, List<String> names) {
    var moduleS = taskExecutor.submit(FrontendCompile.class, argument(modules));
    var compiledExprs = taskExecutor.submit(ScheduleBackendCompile.class, moduleS, argument(names));
    var evaluatedExprs = taskExecutor.submit(VmFacade.class, compiledExprs);

    var label = EVALUATE_LABEL.append("schedule");
    return schedulingOutput(evaluatedExprs, report(label, new Trace(), EXECUTION, list()));
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

      var values = taskExecutor.submit(FindValues.class, argument(scopeS), argument(valueNames));
      var compiledExprs = taskExecutor.submit(BackendCompile.class, values, argument(evaluables));
      return schedulingOutput(compiledExprs, report(label, new Trace(), EXECUTION, list()));
    }
  }
}
