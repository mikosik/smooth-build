package org.smoothbuild.cli.command.list;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.cli.layout.Layout.PROJECT_PATH;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.common.schedule.Output.schedulingOutput;
import static org.smoothbuild.common.schedule.Tasks.argument;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import jakarta.inject.Inject;
import org.smoothbuild.cli.layout.Layout;
import org.smoothbuild.common.log.location.CommandLineLocation;
import org.smoothbuild.common.log.location.FileLocation;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Scheduler;
import org.smoothbuild.common.schedule.Task0;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.compilerfrontend.FrontendCompile;
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
    var sScope = scheduler.submit(FrontendCompile.class, argument(Layout.MODULES));
    var result = scheduler.submit(PrintEvaluables.class, sScope);
    var label = ListCommand.LABEL.append(":schedule");
    return schedulingOutput(result, report(label, list()));
  }

  public static class PrintEvaluables implements Task1<SModule, Tuple0> {
    @Override
    public Output<Tuple0> execute(SModule sModule) {
      var oneValuePerLineString = sModule.evaluables().values().stream()
          .filter(ScheduleList::isNoArgNotGenericValue)
          .map(sNamedEvaluable -> sNamedEvaluable.fqn().toString())
          .sorted()
          .collect(joining("\n"));
      var info = info("Values that can be evaluated:\n" + oneValuePerLineString);
      return output(tuple(), ListCommand.LABEL, list(info));
    }
  }

  private static boolean isNoArgNotGenericValue(SNamedEvaluable evaluable) {
    return isInUserSpace(evaluable.location())
        && evaluable instanceof SNamedValue
        && evaluable.typeScheme().typeParams().isEmpty();
  }

  private static boolean isInUserSpace(Location location) {
    return isInProjectDir(location) || isInCommandLine(location);
  }

  private static boolean isInProjectDir(Location location) {
    return location instanceof FileLocation source && source.path().startsWith(PROJECT_PATH);
  }

  private static boolean isInCommandLine(Location location) {
    return location instanceof CommandLineLocation;
  }
}
