package org.smoothbuild.common.testing;

import static com.google.common.base.Suppliers.memoize;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.task.TaskExecutor;

public class TestingCommon {
  private final Supplier<TaskExecutor> taskExecutor = memoize(this::newTaskExecutor);
  private final com.google.common.base.Supplier<MemoryReporter> reporter =
      memoize(MemoryReporter::new);

  public TaskExecutor taskExecutor(Reporter reporter) {
    return taskExecutor(reporter, 4);
  }

  public TaskExecutor taskExecutor(Reporter reporter, int threadCount) {
    // TaskExecutor has to be created via injector because its submit methods takes Class<?> value
    // argument which is used to create instance of such class via Injector. If that class
    // has TaskExecutor injected as constructor argument then we need create TaskExecutor here by
    // calling Injector.getInstance() so we get the same instance of it.
    var injector = Guice.createInjector(new AbstractModule() {
      @Provides
      @Singleton
      public TaskExecutor provideTaskExecutor(Injector injector) {
        return new TaskExecutor(injector, reporter, threadCount);
      }
    });
    return injector.getInstance(TaskExecutor.class);
  }

  public TaskExecutor taskExecutor() {
    return taskExecutor.get();
  }

  private TaskExecutor newTaskExecutor() {
    return taskExecutor(reporter());
  }

  public MemoryReporter reporter() {
    return reporter.get();
  }
}
