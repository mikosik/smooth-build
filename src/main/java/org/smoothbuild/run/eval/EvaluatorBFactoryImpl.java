package org.smoothbuild.run.eval;

import javax.inject.Inject;

import org.smoothbuild.compile.sb.BsMapping;
import org.smoothbuild.run.eval.report.TaskReporterImpl;
import org.smoothbuild.vm.EvaluatorB;
import org.smoothbuild.vm.execute.TaskReporter;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;

public class EvaluatorBFactoryImpl implements EvaluatorBFactory {
  private final Injector injector;

  @Inject
  public EvaluatorBFactoryImpl(Injector injector) {
    this.injector = injector;
  }

  @Override
  public EvaluatorB newEvaluatorB(BsMapping bsMapping) {
    var childInjector = injector.createChildInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(TaskReporter.class).to(TaskReporterImpl.class);
        bind(BsMapping.class).toInstance(bsMapping);
      }
    });
    return childInjector.getInstance(EvaluatorB.class);
  }
}
