package org.smoothbuild.cli.run;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.common.task.Output.schedulingOutput;
import static org.smoothbuild.common.task.Tasks.argument;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import jakarta.inject.Inject;
import org.smoothbuild.cli.layout.BucketIds;
import org.smoothbuild.cli.layout.Layout;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Scheduler;
import org.smoothbuild.common.task.Task0;
import org.smoothbuild.common.task.Task1;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.compilerfrontend.FrontendCompile;
import org.smoothbuild.compilerfrontend.lang.base.Nal;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.location.SourceLocation;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.lang.define.SNamedValue;

public class ListEvaluables implements Task0<Tuple0> {
  private final Scheduler scheduler;

  @Inject
  public ListEvaluables(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  public Output<Tuple0> execute() {
    var scopeS = scheduler.submit(FrontendCompile.class, argument(Layout.MODULES));
    var result = scheduler.submit(PrintEvaluables.class, scopeS);
    var label = label("schedule", "list");
    return schedulingOutput(result, report(label, new Trace(), EXECUTION, list()));
  }

  public static class PrintEvaluables implements Task1<SModule, Tuple0> {
    @Override
    public Output<Tuple0> execute(SModule sModule) {
      var oneValuePerLineString =
          sModule.membersAndImported().evaluables().toMap().values().stream()
              .filter(ListEvaluables::isNoArgNotGenericValue)
              .map(Nal::name)
              .sorted()
              .collect(joining("\n"));
      var label = label("cli", "list");
      var info = info("Values that can be evaluated:\n" + oneValuePerLineString);
      return output(tuple(), label, list(info));
    }
  }

  private static boolean isNoArgNotGenericValue(SNamedEvaluable evaluable) {
    return isInProjectSpace(evaluable.location())
        && evaluable instanceof SNamedValue
        && evaluable.schema().quantifiedVars().isEmpty();
  }

  private static boolean isInProjectSpace(Location location) {
    return (location instanceof SourceLocation source)
        && BucketIds.PROJECT.equals(source.bucketId());
  }
}
