package org.smoothbuild.cli.command.build;

import static org.smoothbuild.cli.command.base.CreateInjector.createInjector;
import static org.smoothbuild.common.log.base.Label.label;

import java.nio.file.Path;
import org.smoothbuild.cli.command.base.CommandRunner;
import org.smoothbuild.cli.command.base.ProjectCommand;
import org.smoothbuild.cli.match.MatcherCreator;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.report.ReportMatcher;
import picocli.CommandLine.Command;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.Parameters;

@Command(
    name = BuildCommand.NAME,
    description = "Evaluate specified value(s) and store them as artifact(s).")
public class BuildCommand extends ProjectCommand {
  public static final String NAME = "build";
  public static final Label LABEL = label(":cli:build");

  @picocli.CommandLine.Option(
      names = {"--filter-tasks", "-t"},
      defaultValue = "default",
      paramLabel = "<filter>",
      converter = FilterTasksConverter.class,
      description =
          """
          Print executed build tasks that match filter.

          Filter is a boolean expression made up of matchers (listed below), \
          boolean operators '&', '|', grouping brackets '(', ')'.
          Default value is 'info|invoke'

          For each matched tasks its label, trace and logs are printed which can be further \
          filtered using --filter-logs and --filter-stack-traces options. \
          Note that you can filter tasks by one log level and its logs by other level. \
          For example setting '--filter-tasks=error --filter-logs=warning' prints tasks that \
          have a log with at least error level and for each such a task all logs with at \
          least warning level.

          Available task matchers:
            a, all             - all tasks
            d, default         - shortcut for 'info|invoke'
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
  ReportMatcher filterTasks;

  @picocli.CommandLine.Option(
      names = {"--filter-stack-traces", "-s"},
      defaultValue = "error",
      paramLabel = "<filter>",
      converter = FilterTasksConverter.class,
      description =
          """
          Print stack trace for task execution reports that match filter.

          Filter is specified using the same language as used for specifying filters
          in --filter-tasks option.
          """)
  ReportMatcher filterTraces;

  public static class FilterTasksConverter implements ITypeConverter<ReportMatcher> {
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
    var injector = createInjector(projectDir, out(), filterLogs, filterTasks, filterTraces);
    return injector
        .getInstance(CommandRunner.class)
        .run(s -> s.submit(new ScheduleBuild(s, values)));
  }
}
