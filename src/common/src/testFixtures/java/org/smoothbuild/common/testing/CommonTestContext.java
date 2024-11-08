package org.smoothbuild.common.testing;

import static com.google.common.base.Suppliers.memoize;
import static org.smoothbuild.common.bucket.base.Alias.alias;
import static org.smoothbuild.common.bucket.base.FullPath.fullPath;
import static org.smoothbuild.common.bucket.base.Path.path;

import com.google.common.base.Supplier;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.task.Scheduler;

public class CommonTestContext {
  private final Supplier<Scheduler> scheduler = memoize(this::newScheduler);
  private final Supplier<TestReporter> testReporter = memoize(this::newTestReporter);
  private int threadCount = 4;

  public void setThreadCount(int count) {
    this.threadCount = count;
  }

  public Scheduler scheduler() {
    return scheduler.get();
  }

  private Scheduler newScheduler() {
    return new Scheduler(null, reporter(), threadCount);
  }

  public TestReporter reporter() {
    return testReporter.get();
  }

  private TestReporter newTestReporter() {
    return new TestReporter();
  }

  public FullPath moduleFullPath() {
    return moduleFullPath("module.smooth");
  }

  public static FullPath moduleFullPath(String path) {
    return fullPath(alias("t-alias"), path(path));
  }
}
