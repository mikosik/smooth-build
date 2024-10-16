package org.smoothbuild.evaluator;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.common.task.Output.schedulingOutput;
import static org.smoothbuild.evaluator.EvaluatedExprs.evaluatedExprs;
import static org.smoothbuild.evaluator.EvaluatorConstants.EVALUATE_LABEL;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import jakarta.inject.Inject;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task1;
import org.smoothbuild.common.task.TaskExecutor;
import org.smoothbuild.common.task.TaskX;
import org.smoothbuild.compilerbackend.CompiledExprs;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.execute.Vm;
import org.smoothbuild.virtualmachine.wire.TaskReporter;

public class BEvaluatorFacade implements Task1<EvaluatedExprs, CompiledExprs> {
  private final Injector injector;
  private final TaskExecutor taskExecutor;

  @Inject
  public BEvaluatorFacade(Injector injector, TaskExecutor taskExecutor) {
    this.injector = injector;
    this.taskExecutor = taskExecutor;
  }

  @Override
  public Output<EvaluatedExprs> execute(CompiledExprs compiledExprs) {
    var childInjector = injector.createChildInjector(new AbstractModule() {
      @Provides
      @TaskReporter
      public Reporter provideTaskReporter(Reporter reporter) {
        var bsTranslator = new BsTranslator(compiledExprs.bsMapping());
        return new TranslatingReporter(reporter, bsTranslator);
      }
    });
    var vm = childInjector.getInstance(Vm.class);
    List<Promise<BValue>> evaluated = compiledExprs.bExprs().map(vm::evaluate);
    var r = taskExecutor.submit(new Merge(compiledExprs), evaluated);
    return schedulingOutput(r, report(label("??"), new Trace(), EXECUTION, list()));
  }

  public static class Merge implements TaskX<EvaluatedExprs, BValue> {
    private final CompiledExprs compiledExprs;

    public Merge(CompiledExprs compiledExprs) {
      this.compiledExprs = compiledExprs;
    }

    @Override
    public Output<EvaluatedExprs> execute(List<BValue> evaluatedExprs) {
      var evaluatedExprs1 = evaluatedExprs(compiledExprs.sExprs(), evaluatedExprs);
      return output(evaluatedExprs1, EVALUATE_LABEL.append("merge"), list());
    }
  }
}
