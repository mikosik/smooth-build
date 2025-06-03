package org.smoothbuild.cli.command.build;

import jakarta.inject.Inject;
import org.smoothbuild.cli.command.base.CompletionWaiter;
import org.smoothbuild.common.schedule.Scheduler;

public class BuildRunner {
  private final ScheduleBuild scheduleBuild;
  private final Scheduler scheduler;
  private final CompletionWaiter completionWaiter;

  @Inject
  public BuildRunner(
      ScheduleBuild scheduleBuild, Scheduler scheduler, CompletionWaiter completionWaiter) {
    this.scheduleBuild = scheduleBuild;
    this.scheduler = scheduler;
    this.completionWaiter = completionWaiter;
  }

  public int run() {
    var result = scheduler.submit(scheduleBuild);
    return completionWaiter.waitForCompletion(result);
  }
}
