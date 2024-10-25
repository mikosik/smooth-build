package org.smoothbuild.common.testing;

import static com.google.common.base.Suppliers.memoize;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.task.Scheduler;

public class TestingCommon {
  private final Supplier<Scheduler> scheduler = memoize(() -> scheduler(reporter()));
  private final com.google.common.base.Supplier<MemoryReporter> reporter =
      memoize(MemoryReporter::new);

  public Scheduler scheduler(Reporter reporter) {
    return scheduler(reporter, 4);
  }

  public Scheduler scheduler(Reporter reporter, int threadCount) {
    // Scheduler has to be created via injector because its submit methods takes Class<?> value
    // argument which is used to create instance of such class via Injector. If that class
    // has Scheduler injected as constructor argument then we need create Scheduler here by
    // calling Injector.getInstance() so we get the same instance of it.
    var injector = Guice.createInjector(new AbstractModule() {
      @Provides
      @Singleton
      public Scheduler provideScheduler(Injector injector) {
        return new Scheduler(injector, reporter, threadCount);
      }
    });
    return injector.getInstance(Scheduler.class);
  }

  public Scheduler scheduler() {
    return scheduler.get();
  }

  public MemoryReporter reporter() {
    return reporter.get();
  }
}
