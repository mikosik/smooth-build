package org.smoothbuild.common.testing;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import org.smoothbuild.common.log.report.Reporter;

public class CommonTestContext extends CommonTestApi {
  @Override
  protected Module module() {
    return new AbstractModule() {
      @Override
      protected void configure() {
        bind(TestReporter.class).toInstance(new TestReporter());
        bind(Reporter.class).to(TestReporter.class);
      }
    };
  }
}
