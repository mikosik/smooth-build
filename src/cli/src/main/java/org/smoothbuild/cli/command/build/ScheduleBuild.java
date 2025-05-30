package org.smoothbuild.cli.command.build;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.schedulingOutput;
import static org.smoothbuild.common.schedule.Tasks.argument;

import jakarta.inject.Inject;
import java.util.List;
import org.smoothbuild.cli.layout.Layout;
import org.smoothbuild.common.init.Initializer;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Scheduler;
import org.smoothbuild.common.schedule.Task0;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.evaluator.ScheduleEvaluate;

public class ScheduleBuild implements Task0<Tuple0> {
  private final Scheduler scheduler;
  private final List<String> values;
  private final Initializer initializer;
  private final DeleteArtifacts deleteArtifacts;
  private final ScheduleEvaluate scheduleEvaluate;
  private final SaveArtifacts saveArtifacts;

  @Inject
  public ScheduleBuild(
      Scheduler scheduler,
      List<String> values,
      Initializer initializer,
      DeleteArtifacts deleteArtifacts,
      ScheduleEvaluate scheduleEvaluate,
      SaveArtifacts saveArtifacts) {
    this.scheduler = scheduler;
    this.values = values;
    this.initializer = initializer;
    this.deleteArtifacts = deleteArtifacts;
    this.scheduleEvaluate = scheduleEvaluate;
    this.saveArtifacts = saveArtifacts;
  }

  @Override
  public Output<Tuple0> execute() {
    var initialize = scheduler.submit(initializer);
    var removeArtifacts = scheduler.submit(list(initialize), deleteArtifacts);
    var evaluatedExprs = scheduler.submit(
        list(removeArtifacts),
        scheduleEvaluate,
        argument(Layout.MODULES),
        argument(listOfAll(values)));
    var result = scheduler.submit(saveArtifacts, evaluatedExprs);
    var buildLabel = BuildCommand.LABEL.append(":schedule");
    return schedulingOutput(result, report(buildLabel, list()));
  }
}
