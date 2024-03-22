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
import org.smoothbuild.virtualmachine.evaluate.EvaluatorB;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReporter;

public class EvaluatorBFacade implements MaybeFunction<CompiledExprs, EvaluatedExprs> {
  private final Injector injector;

  @Inject
  public EvaluatorBFacade(Injector injector) {
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
    var valuesB = childInjector.getInstance(EvaluatorB.class).evaluate(compiledExprs.exprBs());
    return valuesB.map(v -> evaluatedExprs(compiledExprs.exprSs(), v));
  }
}
