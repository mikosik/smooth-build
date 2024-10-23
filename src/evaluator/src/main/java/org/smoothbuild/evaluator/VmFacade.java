package org.smoothbuild.evaluator;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.task.Output.schedulingOutput;
import static org.smoothbuild.common.task.Tasks.argument;
import static org.smoothbuild.common.task.Tasks.task2;
import static org.smoothbuild.evaluator.EvaluatedExprs.evaluatedExprs;
import static org.smoothbuild.evaluator.EvaluatorConstants.EVALUATE_LABEL;

import jakarta.inject.Inject;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task1;
import org.smoothbuild.common.task.TaskExecutor;
import org.smoothbuild.compilerbackend.CompiledExprs;
import org.smoothbuild.virtualmachine.evaluate.execute.Vm;

public class VmFacade implements Task1<EvaluatedExprs, CompiledExprs> {
  private final TaskExecutor taskExecutor;
  private final Vm vm;

  @Inject
  public VmFacade(TaskExecutor taskExecutor, Vm vm) {
    this.taskExecutor = taskExecutor;
    this.vm = vm;
  }

  @Override
  public Output<EvaluatedExprs> execute(CompiledExprs compiledExprs) {
    var evaluated = taskExecutor.join(compiledExprs.bExprs().map(vm::evaluate));
    var mergeLabel = EVALUATE_LABEL.append("merge");
    var evaluatedExprs = taskExecutor.submit(
        task2(mergeLabel, (ce, ee) -> evaluatedExprs(ce.sExprs(), ee)),
        argument(compiledExprs),
        evaluated);
    return schedulingOutput(evaluatedExprs, report(label("??"), new Trace(), EXECUTION, list()));
  }
}
