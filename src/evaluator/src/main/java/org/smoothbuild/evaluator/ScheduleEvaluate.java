package org.smoothbuild.evaluator;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.schedulingOutput;
import static org.smoothbuild.common.schedule.Tasks.argument;
import static org.smoothbuild.common.schedule.Tasks.task1;
import static org.smoothbuild.common.schedule.Tasks.task2;
import static org.smoothbuild.common.tuple.Tuples.tuple;
import static org.smoothbuild.evaluator.EvaluatorConstants.EVALUATOR_LABEL;

import jakarta.inject.Inject;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Scheduler;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.compilerbackend.BackendCompile;
import org.smoothbuild.compilerbackend.CompiledExprs;
import org.smoothbuild.compilerfrontend.FrontendCompile;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.lang.define.SPolyEvaluable;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.compilerfrontend.lang.name.Bindings;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.evaluate.execute.BEvaluate;
import org.smoothbuild.virtualmachine.evaluate.execute.BExprAttributes;

public class ScheduleEvaluate implements Task2<List<FullPath>, List<String>, EvaluatedExprs> {
  private final Scheduler scheduler;

  @Inject
  public ScheduleEvaluate(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  public Output<EvaluatedExprs> execute(List<FullPath> modules, List<String> names) {
    var sModule = scheduler.submit(FrontendCompile.class, argument(modules));
    var mapLabel = EVALUATOR_LABEL.append(":getMembersAndImported");
    var sScope = scheduler.submit(task1(mapLabel, SModule::scope), sModule);
    var sExprs = scheduler.submit(FindValues.class, sScope, argument(names));
    var evaluables = scheduler.submit(task1(mapLabel, SScope::evaluables), sScope);

    var evaluatedExprs = scheduler.submit(EvaluateCore.class, sExprs, evaluables);

    var scheduleLabel = EVALUATOR_LABEL.append(":schedule");
    return schedulingOutput(evaluatedExprs, report(scheduleLabel, list()));
  }

  public static class EvaluateCore
      implements Task2<List<SExpr>, Bindings<SPolyEvaluable>, EvaluatedExprs> {
    private final Scheduler scheduler;

    @Inject
    public EvaluateCore(Scheduler scheduler) {
      this.scheduler = scheduler;
    }

    @Override
    public Output<EvaluatedExprs> execute(List<SExpr> sExprs, Bindings<SPolyEvaluable> evaluables) {
      var compiledExprs =
          scheduler.submit(BackendCompile.class, argument(sExprs), argument(evaluables));
      var getLabel = EVALUATOR_LABEL.append(":getCompiledExprs");
      var bExprs = scheduler.submit(task1(getLabel, this::toTuples), compiledExprs);
      var evaluated = scheduler.submit(scheduler.newParallelTask(BEvaluate.class), bExprs);
      var mergeLabel = EVALUATOR_LABEL.append(":merge");
      var evaluate =
          scheduler.submit(task2(mergeLabel, EvaluatedExprs::new), argument(sExprs), evaluated);
      var scheduleLabel = EVALUATOR_LABEL.append(":scheduleEvaluate");
      return schedulingOutput(evaluate, report(scheduleLabel, list()));
    }

    private List<Tuple2<BExpr, BExprAttributes>> toTuples(CompiledExprs compiledExprs) {
      return compiledExprs.bExprs().map(e -> tuple(e, compiledExprs.bExprAttributes()));
    }
  }
}
