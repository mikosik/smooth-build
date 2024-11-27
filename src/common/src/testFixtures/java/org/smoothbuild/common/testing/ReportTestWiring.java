package org.smoothbuild.common.testing;

import com.google.inject.AbstractModule;
import org.smoothbuild.common.log.report.ReportMatcher;
import org.smoothbuild.common.log.report.Reporter;

public class ReportTestWiring extends AbstractModule {
  @Override
  protected void configure() {
    bind(TestReporter.class).toInstance(new TestReporter());
    bind(Reporter.class).to(TestReporter.class);
    bind(ReportMatcher.class).toInstance((label, logs) -> true);
  }
}
