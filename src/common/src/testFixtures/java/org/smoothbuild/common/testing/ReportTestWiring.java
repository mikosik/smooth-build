package org.smoothbuild.common.testing;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import java.util.function.Predicate;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.log.report.Reporter;

public class ReportTestWiring extends AbstractModule {
  @Override
  protected void configure() {
    bind(TestReporter.class).toInstance(new TestReporter());
    bind(Reporter.class).to(TestReporter.class);
    bind(Key.get(new TypeLiteral<Predicate<Report>>() {})).toInstance(report -> true);
  }
}
