package org.smoothbuild.cli.command.build;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.task.Output.schedulingOutput;
import static org.smoothbuild.common.task.Tasks.argument;

import java.util.List;
import org.smoothbuild.cli.layout.Layout;
import org.smoothbuild.common.init.Initializer;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Scheduler;
import org.smoothbuild.common.task.Task0;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.evaluator.ScheduleEvaluate;

public class ScheduleBuild implements Task0<Tuple0> {
  private final Scheduler scheduler;
  private final java.util.List<String> values;

  public ScheduleBuild(Scheduler scheduler, List<String> values) {
    this.scheduler = scheduler;
    this.values = values;
  }

  @Override
  public Output<Tuple0> execute() {
    var initialize = scheduler.submit(Initializer.class);
    var removeArtifacts = scheduler.submit(list(initialize), DeleteArtifacts.class);
    var evaluatedExprs = scheduler.submit(
        list(removeArtifacts),
        ScheduleEvaluate.class,
        argument(Layout.MODULES),
        argument(listOfAll(values)));
    var result = scheduler.submit(SaveArtifacts.class, evaluatedExprs);
    var buildLabel = BuildCommand.LABEL.append("schedule");
    return schedulingOutput(result, report(buildLabel, list()));
  }
}
