package org.smoothbuild.common.testing;

import static com.google.common.base.Suppliers.memoize;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.function.Supplier;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.task.TaskExecutor;

public class TestingCommon {
  private final Supplier<TaskExecutor> taskExecutor = memoize(this::newTaskExecutor);
  private final com.google.common.base.Supplier<MemoryReporter> reporter =
      memoize(MemoryReporter::new);

  public TaskExecutor taskExecutor(Reporter reporter) {
    return taskExecutor(Guice.createInjector(), reporter);
  }

  private static TaskExecutor taskExecutor(Injector injector, Reporter reporter) {
    return new TaskExecutor(injector, reporter, 4);
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
