package org.smoothbuild.cli.command.clean;

import jakarta.inject.Inject;
import org.smoothbuild.cli.command.base.CompletionWaiter;
import org.smoothbuild.common.schedule.Scheduler;

public class CleanRunner {
  private final Clean clean;
  private final Scheduler scheduler;
  private final CompletionWaiter completionWaiter;

  @Inject
  public CleanRunner(Clean clean, Scheduler scheduler, CompletionWaiter completionWaiter) {
    this.clean = clean;
    this.scheduler = scheduler;
    this.completionWaiter = completionWaiter;
  }

  public int run() {
    var result = scheduler.submit(clean);
    return completionWaiter.waitForCompletion(result);
  }
}
