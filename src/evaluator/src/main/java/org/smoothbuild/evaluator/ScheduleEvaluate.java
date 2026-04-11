package org.smoothbuild.evaluator;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.schedulingOutput;
import static org.smoothbuild.common.schedule.Tasks.argument;
import static org.smoothbuild.common.schedule.Tasks.task1;
import static org.smoothbuild.common.schedule.Tasks.task2;
import static org.smoothbuild.evaluator.EvaluatorConstants.EVALUATOR_LABEL;

import jakarta.inject.Inject;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Scheduler;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.compilerbackend.BackendCompile;
import org.smoothbuild.compilerbackend.CompiledExprs;
import org.smoothbuild.compilerfrontend.FrontendCompile;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.lang.define.SPolyEvaluable;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.compilerfrontend.lang.name.Bindings;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.dagger.VmComponent;
import org.smoothbuild.virtualmachine.evaluate.BEvaluateTask;

public class ScheduleEvaluate implements Task2<List<FullPath>, List<String>, EvaluatedExprs> {
  private final Scheduler scheduler;
  private final FrontendCompile frontendCompile;
  private final EvaluateCore evaluateCore;

  @Inject
  public ScheduleEvaluate(
      Scheduler scheduler, FrontendCompile frontendCompile, EvaluateCore evaluateCore) {
    this.scheduler = scheduler;
    this.frontendCompile = frontendCompile;
    this.evaluateCore = evaluateCore;
  }

  @Override
  public Output<EvaluatedExprs> execute(List<FullPath> modules, List<String> names) {
    var sModule = scheduler.submit(frontendCompile, argument(modules));
    var mapLabel = EVALUATOR_LABEL.append(":getMembersAndImported");
    var sScope = scheduler.submit(task1(mapLabel, SModule::scope), sModule);
    var sExprs = scheduler.submit(new FindValues(), sScope, argument(names));
    var evaluables = scheduler.submit(task1(mapLabel, SScope::evaluables), sScope);
    var evaluatedExprs = scheduler.submit(evaluateCore, sExprs, evaluables);
    var scheduleLabel = EVALUATOR_LABEL.append(":scheduleFrontendCompile");
    return schedulingOutput(evaluatedExprs, report(scheduleLabel, list()));
  }

  public static class EvaluateCore
      implements Task2<List<SExpr>, Bindings<SPolyEvaluable>, EvaluatedExprs> {
    private final Scheduler scheduler;
    private final BackendCompile backendCompile;
    private final EvaluateOnVm evaluateOnVm;

    @Inject
    public EvaluateCore(
        Scheduler scheduler, BackendCompile backendCompile, EvaluateOnVm evaluateOnVm) {
      this.scheduler = scheduler;
      this.backendCompile = backendCompile;
      this.evaluateOnVm = evaluateOnVm;
    }

    @Override
    public Output<EvaluatedExprs> execute(List<SExpr> sExprs, Bindings<SPolyEvaluable> evaluables) {
      var compiledExprs = scheduler.submit(backendCompile, argument(sExprs), argument(evaluables));
      var evaluate = scheduler.submit(evaluateOnVm, compiledExprs);
      var scheduleLabel = EVALUATOR_LABEL.append(":scheduleBackendCompile");
      return schedulingOutput(evaluate, report(scheduleLabel, list()));
    }
  }

  public static class EvaluateOnVm implements Task1<CompiledExprs, EvaluatedExprs> {
    private final VmComponent.Builder vmComponentBuilder;
    private final Scheduler scheduler;

    @Inject
    public EvaluateOnVm(VmComponent.Builder vmComponentBuilder, Scheduler scheduler) {
      this.scheduler = scheduler;
      this.vmComponentBuilder = vmComponentBuilder;
    }

    @Override
    public Output<EvaluatedExprs> execute(CompiledExprs compiledExprs) {
      var bEvaluateTask = bEvaluateTask(compiledExprs);
      var getBExprs = EVALUATOR_LABEL.append(":getBExprs");
      var bExprs = scheduler.submit(task1(getBExprs, this::getBExprs), argument(compiledExprs));
      var bValues = scheduler.submit(scheduler.newParallelTask(bEvaluateTask), bExprs);
      var mergeLabel = EVALUATOR_LABEL.append(":merge");
      var evaluatedExprs = scheduler.submit(
          task2(mergeLabel, EvaluatedExprs::evaluatedExprs), argument(compiledExprs), bValues);
      var scheduleLabel = EVALUATOR_LABEL.append(":scheduleVmEvaluation");
      return schedulingOutput(evaluatedExprs, report(scheduleLabel, list()));
    }

    private BEvaluateTask bEvaluateTask(CompiledExprs compiledExprs) {
      var debugSymbols = compiledExprs.debugSymbols();
      var virtualMachineComponent =
          vmComponentBuilder.debugSymbols(debugSymbols).build();
      return virtualMachineComponent.bEvaluateTask();
    }

    private List<BExpr> getBExprs(CompiledExprs compiledExprs) {
      return compiledExprs.bExprs();
    }
  }
}
