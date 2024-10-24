package org.smoothbuild.evaluator;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.task.Output.schedulingOutput;
import static org.smoothbuild.common.task.Tasks.argument;
import static org.smoothbuild.common.task.Tasks.task1;
import static org.smoothbuild.evaluator.EvaluatorConstants.EVALUATE_LABEL;

import jakarta.inject.Inject;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Scheduler;
import org.smoothbuild.common.task.Task2;
import org.smoothbuild.compilerbackend.BackendCompile;
import org.smoothbuild.compilerfrontend.FrontendCompile;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.lang.define.SScope;

public class ScheduleEvaluate implements Task2<EvaluatedExprs, List<FullPath>, List<String>> {
  private final Scheduler scheduler;

  @Inject
  public ScheduleEvaluate(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  public Output<EvaluatedExprs> execute(List<FullPath> modules, List<String> names) {
    var moduleS = scheduler.submit(FrontendCompile.class, argument(modules));
    var mapLabel = EVALUATE_LABEL.append("map");
    var scopeS = scheduler.submit(task1(mapLabel, SModule::membersAndImported), moduleS);
    var values = scheduler.submit(FindValues.class, scopeS, argument(names));
    var evaluables = scheduler.submit(task1(mapLabel, SScope::evaluables), scopeS);
    var compiledExprs = scheduler.submit(BackendCompile.class, values, evaluables);
    var setBsMapping = scheduler.submit(ConfigureBsTranslator.class, compiledExprs);
    var evaluatedExprs = scheduler.submit(list(setBsMapping), VmFacade.class, compiledExprs);

    var label = EVALUATE_LABEL.append("schedule");
    return schedulingOutput(evaluatedExprs, report(label, new Trace(), EXECUTION, list()));
  }
}
