package org.smoothbuild.evaluator;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.smoothbuild.common.log.report.ReportDecorator;

public class EvaluatorWiring extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder<ReportDecorator> setBinder = newSetBinder(binder(), ReportDecorator.class);
    setBinder.addBinding().to(BsTranslatingReportDecorator.class);
  }
}
