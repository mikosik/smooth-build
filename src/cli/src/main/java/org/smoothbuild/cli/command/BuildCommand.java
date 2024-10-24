package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.run.CreateInjector.createInjector;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.task.Tasks.argument;

import jakarta.inject.Inject;
import java.nio.file.Path;
import java.util.List;
import org.smoothbuild.cli.layout.Layout;
import org.smoothbuild.cli.match.MatcherCreator;
import org.smoothbuild.cli.run.RemoveArtifacts;
import org.smoothbuild.cli.run.SaveArtifacts;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.init.Initializer;
import org.smoothbuild.common.log.report.ReportMatcher;
import org.smoothbuild.common.task.Scheduler;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.evaluator.ScheduleEvaluate;
import picocli.CommandLine.Command;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.Parameters;

@Command(
    name = BuildCommand.NAME,
    description = "Evaluate specified value(s) and store them as artifact(s).")
public class BuildCommand extends ProjectCommand {
  public static final String NAME = "build";

  @picocli.CommandLine.Option(
      names = {"--show-tasks", "-s"},
      defaultValue = "default",
      paramLabel = "<filter>",
      converter = ShowTasksConverter.class,
      description =
          """
          Show executed build tasks that match filter.

          Filter is a boolean expression made up of matchers (listed below), \
          boolean operators '&', '|', grouping brackets '(', ')'.
          Default value is 'info|call'

          For each matched tasks its name and properties are printed together with logs that \
          match filter specified with --log-level option. \
          Note that you can filter tasks by one log level and its logs by other level. \
          For example setting '--show-tasks=error --log-level=warning' prints tasks that \
          have a log with at least error level and for each such a task all logs with at \
          least warning level.

          Available task matchers:
            a, all             - all tasks
            d, default         - shortcut for 'info|call'
            n, none            - no tasks

            lf, fatal          - contains a log with fatal level
            le, error          - contains a log with at least error level
            lw, warning        - contains a log with at least warning level
            li, info           - contains any log

            combine            - evaluates tuple creation
            invoke             - evaluates native function call
            order              - evaluates array creation
            pick               - evaluates array element picking
            select             - evaluates field selection
          """)
  ReportMatcher showTasks;

  public static class ShowTasksConverter implements ITypeConverter<ReportMatcher> {
    @Override
    public ReportMatcher convert(String value) {
      return MatcherCreator.createMatcher(value);
    }
  }

  @Parameters(
      paramLabel = "<value>",
      arity = "1..*",
      description = "value(s) to evaluate and store as artifact(s)")
  java.util.List<String> values;

  @Override
  protected Integer executeCommand(Path projectDir) {
    var injector = createInjector(projectDir, out(), logLevel, showTasks);
    return injector.getInstance(BuildCommandRunner.class).run(values);
  }

  public static class BuildCommandRunner {
    private final Scheduler scheduler;
    private final CommandCompleter commandCompleter;

    @Inject
    public BuildCommandRunner(Scheduler scheduler, CommandCompleter commandCompleter) {
      this.scheduler = scheduler;
      this.commandCompleter = commandCompleter;
    }

    public int run(java.util.List<String> values) {
      return commandCompleter.waitForCompletion(scheduleBuildTasks(values));
    }

    private Promise<Maybe<Tuple0>> scheduleBuildTasks(List<String> values) {
      var initialize = scheduler.submit(Initializer.class);
      var removeArtifacts = scheduler.submit(list(initialize), RemoveArtifacts.class);
      var evaluatedExprs = scheduler.submit(
          list(removeArtifacts),
          ScheduleEvaluate.class,
          argument(Layout.MODULES),
          argument(listOfAll(values)));
      return scheduler.submit(SaveArtifacts.class, evaluatedExprs);
    }
  }
}
