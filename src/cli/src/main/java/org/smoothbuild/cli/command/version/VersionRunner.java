package org.smoothbuild.cli.command.version;

import jakarta.inject.Inject;
import org.smoothbuild.cli.command.base.CompletionWaiter;
import org.smoothbuild.common.schedule.Scheduler;

public class VersionRunner {
  private final Version version;
  private final Scheduler scheduler;
  private final CompletionWaiter completionWaiter;

  @Inject
  public VersionRunner(Version version, Scheduler scheduler, CompletionWaiter completionWaiter) {
    this.version = version;
    this.scheduler = scheduler;
    this.completionWaiter = completionWaiter;
  }

  public int run() {
    var result = scheduler.submit(version);
    return completionWaiter.waitForCompletion(result);
  }
}
