package org.smoothbuild.common.testing;

import static com.google.common.base.Suppliers.memoize;

import com.google.common.base.Supplier;
import org.smoothbuild.common.schedule.Scheduler;

public class CommonTestContext implements CommonTestApi {
  private final Supplier<Scheduler> scheduler = memoize(this::newScheduler);
  private final Supplier<TestReporter> testReporter = memoize(this::newTestReporter);
  private int threadCount = 4;

  public void setThreadCount(int count) {
    this.threadCount = count;
  }

  @Override
  public Scheduler scheduler() {
    return scheduler.get();
  }

  private Scheduler newScheduler() {
    return new Scheduler(null, reporter());
  }

  @Override
  public TestReporter reporter() {
    return testReporter.get();
  }

  private TestReporter newTestReporter() {
    return new TestReporter();
  }
}
