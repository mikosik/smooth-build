package org.smoothbuild.evaluator;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.task.Output.schedulingOutput;
import static org.smoothbuild.common.task.Tasks.argument;
import static org.smoothbuild.common.task.Tasks.task1;
import static org.smoothbuild.common.task.Tasks.task2;
import static org.smoothbuild.evaluator.EvaluatedExprs.evaluatedExprs;
import static org.smoothbuild.evaluator.EvaluatorConstants.EVALUATOR_LABEL;

import com.google.common.annotations.VisibleForTesting;
import jakarta.inject.Inject;
import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Scheduler;
import org.smoothbuild.common.task.Task2;
import org.smoothbuild.compilerbackend.BackendCompile;
import org.smoothbuild.compilerbackend.CompiledExprs;
import org.smoothbuild.compilerfrontend.FrontendCompile;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.virtualmachine.evaluate.execute.BEvaluate;

public class ScheduleEvaluate implements Task2<List<FullPath>, List<String>, EvaluatedExprs> {
  private final Scheduler scheduler;

  @Inject
  public ScheduleEvaluate(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  public Output<EvaluatedExprs> execute(List<FullPath> modules, List<String> names) {
    var moduleS = scheduler.submit(FrontendCompile.class, argument(modules));
    var mapLabel = EVALUATOR_LABEL.append("getMembersAndImported");
    var scopeS = scheduler.submit(task1(mapLabel, SModule::membersAndImported), moduleS);
    var sExprs = scheduler.submit(FindValues.class, scopeS, argument(names));
    var evaluables = scheduler.submit(task1(mapLabel, SScope::evaluables), scopeS);

    var evaluatedExprs = scheduleEvaluateCore(scheduler, sExprs, evaluables);

    var scheduleLabel = EVALUATOR_LABEL.append("schedule");
    return schedulingOutput(evaluatedExprs, report(scheduleLabel, new Trace(), EXECUTION, list()));
  }

  @VisibleForTesting
  public static Promise<Maybe<EvaluatedExprs>> scheduleEvaluateCore(
      Scheduler scheduler,
      Promise<Maybe<List<SExpr>>> sExprs,
      Promise<Maybe<ImmutableBindings<SNamedEvaluable>>> evaluables) {
    var compiledExprs = scheduler.submit(BackendCompile.class, sExprs, evaluables);
    var setBsMapping = scheduler.submit(ConfigureBsTranslator.class, compiledExprs);
    var getLabel = EVALUATOR_LABEL.append("getCompiledExprs");
    var bExprs = scheduler.submit(task1(getLabel, CompiledExprs::bExprs), compiledExprs);
    var evaluated =
        scheduler.submit(list(setBsMapping), scheduler.newParallelTask(BEvaluate.class), bExprs);
    var mergeLabel = EVALUATOR_LABEL.append("merge");
    return scheduler.submit(
        task2(mergeLabel, (ce, ee) -> evaluatedExprs(ce.sExprs(), ee)), compiledExprs, evaluated);
  }
}
