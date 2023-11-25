package org.smoothbuild.run.eval;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.compile.backend.BsMapping;
import org.smoothbuild.run.eval.report.TaskReporterImpl;
import org.smoothbuild.run.step.OptionFunction;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.EvaluatorB;
import org.smoothbuild.vm.evaluate.execute.TaskReporter;

public class EvaluatorBFacade
    implements OptionFunction<Tuple2<List<ExprB>, BsMapping>, List<ValueB>> {
  private final Injector injector;

  @Inject
  public EvaluatorBFacade(Injector injector) {
    this.injector = injector;
  }

  @Override
  public Option<List<ValueB>> apply(Tuple2<List<ExprB>, BsMapping> argument) {
    var bsMapping = argument._2();
    var exprs = argument._1();
    var childInjector = injector.createChildInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(TaskReporter.class).to(TaskReporterImpl.class);
        bind(BsMapping.class).toInstance(bsMapping);
      }
    });
    var evaluatorB = childInjector.getInstance(EvaluatorB.class);
    return evaluatorB.evaluate(exprs);
  }
}
