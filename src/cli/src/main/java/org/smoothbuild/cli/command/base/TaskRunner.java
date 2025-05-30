package org.smoothbuild.cli.command.base;

import jakarta.inject.Inject;
import org.smoothbuild.common.schedule.Scheduler;
import org.smoothbuild.common.schedule.Task0;
import org.smoothbuild.common.tuple.Tuple0;

public class TaskRunner<T extends Task0<Tuple0>> {
  private final T task;
  private final Scheduler scheduler;
  private final CompletionWaiter completionWaiter;

  @Inject
  public TaskRunner(T task, Scheduler scheduler, CompletionWaiter completionWaiter) {
    this.task = task;
    this.scheduler = scheduler;
    this.completionWaiter = completionWaiter;
  }

  public int run() {
    var result = scheduler.submit(task);
    return completionWaiter.waitForCompletion(result);
  }
}
