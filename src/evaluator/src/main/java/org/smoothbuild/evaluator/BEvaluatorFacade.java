package org.smoothbuild.evaluator;

import static org.smoothbuild.evaluator.EvaluatedExprs.evaluatedExprs;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import jakarta.inject.Inject;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.dag.MaybeFunction;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.compilerbackend.CompiledExprs;
import org.smoothbuild.virtualmachine.evaluate.BEvaluator;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReporter;

public class BEvaluatorFacade implements MaybeFunction<CompiledExprs, EvaluatedExprs> {
  private final Injector injector;

  @Inject
  public BEvaluatorFacade(Injector injector) {
    this.injector = injector;
  }

  @Override
  public Maybe<EvaluatedExprs> apply(CompiledExprs compiledExprs) {
    var childInjector = injector.createChildInjector(new AbstractModule() {
      @Provides
      public TaskReporter provideTaskReporter(Reporter reporter) {
        var bsTranslator = new BsTranslator(compiledExprs.bsMapping());
        return new TaskReporterImpl(reporter, bsTranslator);
      }
    });
    var bValues = childInjector.getInstance(BEvaluator.class).evaluate(compiledExprs.bExprs());
    return bValues.map(v -> evaluatedExprs(compiledExprs.sExprs(), v));
  }
}
