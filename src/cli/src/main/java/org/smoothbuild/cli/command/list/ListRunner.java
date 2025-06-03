package org.smoothbuild.cli.command.list;

import jakarta.inject.Inject;
import org.smoothbuild.cli.command.base.CompletionWaiter;
import org.smoothbuild.common.schedule.Scheduler;

public class ListRunner {
  private final ScheduleList scheduleList;
  private final Scheduler scheduler;
  private final CompletionWaiter completionWaiter;

  @Inject
  public ListRunner(
      ScheduleList scheduleList, Scheduler scheduler, CompletionWaiter completionWaiter) {
    this.scheduleList = scheduleList;
    this.scheduler = scheduler;
    this.completionWaiter = completionWaiter;
  }

  public int run() {
    var result = scheduler.submit(scheduleList);
    return completionWaiter.waitForCompletion(result);
  }
}
