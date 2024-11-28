package org.smoothbuild.evaluator;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.schedulingOutput;
import static org.smoothbuild.common.schedule.Tasks.argument;
import static org.smoothbuild.common.schedule.Tasks.task1;
import static org.smoothbuild.common.schedule.Tasks.task2;
import static org.smoothbuild.common.tuple.Tuples.tuple;
import static org.smoothbuild.evaluator.EvaluatorConstants.EVALUATOR_LABEL;

import com.google.common.annotations.VisibleForTesting;
import jakarta.inject.Inject;
import org.smoothbuild.common.bindings.ImmutableBindings;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.log.report.BExprAttributes;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Scheduler;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.compilerbackend.BackendCompile;
import org.smoothbuild.compilerbackend.CompiledExprs;
import org.smoothbuild.compilerfrontend.FrontendCompile;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
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
    return schedulingOutput(evaluatedExprs, report(scheduleLabel, list()));
  }

  @VisibleForTesting
  public static Promise<Maybe<EvaluatedExprs>> scheduleEvaluateCore(
      Scheduler scheduler,
      Promise<Maybe<List<SExpr>>> sExprs,
      Promise<Maybe<ImmutableBindings<SNamedEvaluable>>> evaluables) {
    var compiledExprs = scheduler.submit(BackendCompile.class, sExprs, evaluables);
    var getLabel = EVALUATOR_LABEL.append("getCompiledExprs");
    var bExprs = scheduler.submit(task1(getLabel, ScheduleEvaluate::toTuples), compiledExprs);
    var evaluated = scheduler.submit(scheduler.newParallelTask(BEvaluate.class), bExprs);
    var mergeLabel = EVALUATOR_LABEL.append("merge");
    return scheduler.submit(task2(mergeLabel, EvaluatedExprs::new), sExprs, evaluated);
  }

  private static List<Tuple2<BExpr, BExprAttributes>> toTuples(CompiledExprs compiledExprs) {
    return compiledExprs.bExprs().map(e -> tuple(e, compiledExprs.bExprAttributes()));
  }
}
