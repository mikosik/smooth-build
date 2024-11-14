package org.smoothbuild.common.task;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.task.Output.schedulingOutput;
import static org.smoothbuild.common.task.Tasks.argument;

import org.smoothbuild.common.collect.List;

public class ParallelTask<A1, R> implements Task1<List<? extends A1>, List<R>> {
  private final Scheduler scheduler;
  private final Task1<A1, R> task;

  ParallelTask(Scheduler scheduler, Task1<A1, R> task) {
    this.scheduler = scheduler;
    this.task = task;
  }

  @Override
  public Output<List<R>> execute(List<? extends A1> list) {
    var mappedList = list.map(a -> scheduler.submit(task, argument(a)));
    var joined = scheduler.join(mappedList);
    var report = report(Scheduler.LABEL.append("scheduleParallel"), list());
    return schedulingOutput(joined, report);
  }
}
