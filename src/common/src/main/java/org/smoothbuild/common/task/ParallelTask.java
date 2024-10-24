package org.smoothbuild.common.task;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.task.Output.schedulingOutput;
import static org.smoothbuild.common.task.Tasks.argument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;

public class ParallelTask<R, A1> implements Task1<List<R>, List<? extends A1>> {
  private final Scheduler scheduler;
  private final Task1<R, A1> task;

  ParallelTask(Scheduler scheduler, Task1<R, A1> task) {
    this.scheduler = scheduler;
    this.task = task;
  }

  @Override
  public Output<List<R>> execute(List<? extends A1> list) {
    var mappedList = list.map(a -> scheduler.submit(task, argument(a)));
    var joined = scheduler.join(mappedList);
    var report = report(Scheduler.LABEL.append("scheduleParallel"), new Trace(), EXECUTION, list());
    return schedulingOutput(joined, report);
  }
}
