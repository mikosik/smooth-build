package org.smoothbuild.evaluator;

import static org.smoothbuild.common.log.report.MappingReporter.labelPrefixingReporter;
import static org.smoothbuild.evaluator.EvaluateConstants.EVALUATE;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import jakarta.inject.Inject;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.dag.MaybeFunction;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.compilerbackend.BsMapping;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.EvaluatorB;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReporter;

public class EvaluatorBFacade
    implements MaybeFunction<Tuple2<List<ExprB>, BsMapping>, List<ValueB>> {
  private final Injector injector;

  @Inject
  public EvaluatorBFacade(Injector injector) {
    this.injector = injector;
  }

  @Override
  public Maybe<List<ValueB>> apply(Tuple2<List<ExprB>, BsMapping> argument) {
    var childInjector = injector.createChildInjector(new AbstractModule() {
      @Provides
      public TaskReporter provideTaskReporter(Reporter reporter) {
        var bsMapping = argument.element2();
        var bsTranslator = new BsTranslator(bsMapping);
        return new TaskReporterImpl(labelPrefixingReporter(reporter, EVALUATE), bsTranslator);
      }
    });
    var exprs = argument.element1();
    return childInjector.getInstance(EvaluatorB.class).evaluate(exprs);
  }
}
