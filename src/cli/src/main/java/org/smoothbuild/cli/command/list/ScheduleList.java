package org.smoothbuild.cli.command.list;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.cli.layout.Aliases.PROJECT_ALIAS;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.common.task.Output.schedulingOutput;
import static org.smoothbuild.common.task.Tasks.argument;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import jakarta.inject.Inject;
import org.smoothbuild.cli.layout.Layout;
import org.smoothbuild.common.log.location.CommandLineLocation;
import org.smoothbuild.common.log.location.FileLocation;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Scheduler;
import org.smoothbuild.common.task.Task0;
import org.smoothbuild.common.task.Task1;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.compilerfrontend.FrontendCompile;
import org.smoothbuild.compilerfrontend.lang.base.Nal;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.lang.define.SNamedValue;

public class ScheduleList implements Task0<Tuple0> {
  private final Scheduler scheduler;

  @Inject
  public ScheduleList(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  public Output<Tuple0> execute() {
    var scopeS = scheduler.submit(FrontendCompile.class, argument(Layout.MODULES));
    var result = scheduler.submit(PrintEvaluables.class, scopeS);
    var label = ListCommand.LABEL.append("schedule");
    return schedulingOutput(result, report(label, list()));
  }

  public static class PrintEvaluables implements Task1<SModule, Tuple0> {
    @Override
    public Output<Tuple0> execute(SModule sModule) {
      var oneValuePerLineString =
          sModule.membersAndImported().evaluables().toMap().values().stream()
              .filter(ScheduleList::isNoArgNotGenericValue)
              .map(Nal::name)
              .sorted()
              .collect(joining("\n"));
      var info = info("Values that can be evaluated:\n" + oneValuePerLineString);
      return output(tuple(), ListCommand.LABEL, list(info));
    }
  }

  private static boolean isNoArgNotGenericValue(SNamedEvaluable evaluable) {
    return isInUserSpace(evaluable.location())
        && evaluable instanceof SNamedValue
        && evaluable.schema().quantifiedVars().isEmpty();
  }

  private static boolean isInUserSpace(Location location) {
    return isInProjectBucket(location) || isInCommandLine(location);
  }

  private static boolean isInProjectBucket(Location location) {
    return location instanceof FileLocation source && PROJECT_ALIAS.equals(source.alias());
  }

  private static boolean isInCommandLine(Location location) {
    return location instanceof CommandLineLocation;
  }
}
