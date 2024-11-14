package org.smoothbuild.common.init;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.init.Initializable.INITIALIZE_LABEL;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.task.Output.schedulingOutput;

import jakarta.inject.Inject;
import java.util.Set;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Scheduler;
import org.smoothbuild.common.task.Task0;
import org.smoothbuild.common.tuple.Tuple0;

public class Initializer implements Task0<List<Tuple0>> {
  private final Set<Initializable> initializables;
  private final Scheduler scheduler;

  @Inject
  public Initializer(Set<Initializable> initializables, Scheduler scheduler) {
    this.initializables = initializables;
    this.scheduler = scheduler;
  }

  @Override
  public Output<List<Tuple0>> execute() {
    var initializablePromises = listOfAll(initializables).map(scheduler::submit);
    var promise = scheduler.join(initializablePromises);
    var report = report(INITIALIZE_LABEL.append("schedule"), new Trace(), EXECUTION, list());
    return schedulingOutput(promise, report);
  }
}
