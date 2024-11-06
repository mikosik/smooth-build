package org.smoothbuild.common.testing;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import org.smoothbuild.common.log.report.DecoratingReporter;
import org.smoothbuild.common.log.report.ReportDecorator;
import org.smoothbuild.common.log.report.ReportMatcher;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.task.SchedulerWiring;

public class CommonTestWiring extends AbstractModule {
  @Override
  protected void configure() {
    install(new SchedulerWiring());
    bind(TestReporter.class).toInstance(new TestReporter());
    bind(ReportMatcher.class).toInstance((label, logs) -> true);
    newSetBinder(binder(), ReportDecorator.class);
  }

  @Provides
  @Singleton
  public Reporter provideReporter(
      TestReporter testReporter, java.util.Set<ReportDecorator> decorators) {
    return new DecoratingReporter(testReporter, decorators);
  }
}
