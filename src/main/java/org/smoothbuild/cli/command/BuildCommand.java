package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.base.CreateInjector.createInjector;
import static org.smoothbuild.cli.taskmatcher.MatcherCreator.createMatcher;

import java.nio.file.Path;
import java.util.List;

import org.smoothbuild.cli.base.ExclusiveCommand;
import org.smoothbuild.cli.taskmatcher.TaskMatcher;
import org.smoothbuild.exec.run.BuildRunner;

import picocli.CommandLine.Command;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = BuildCommand.NAME,
    description = "Build artifact(s) by running specified function(s)"
)
public class BuildCommand extends ExclusiveCommand {
  public static final String NAME = "build";

  @Option(
      names = {"--show-tasks", "-s"},
      defaultValue = "default",
      paramLabel = "<filter>",
      converter = ShowTasksConverter.class,
      description = {
          "Show executed build tasks that match filter.",
          "",
          "Filter is a boolean expression made up of matchers (listed below), " +
              "boolean operators '&', '|', grouping brackets '(', ')'.",
          "Default value is '(user&call)|info'",
          "",
          "For each matched tasks its name and properties are printed together with logs that " +
              "match filter specified with --log-level option. " +
              "Note that you can filter tasks by one log level and its logs by other level. " +
              "For example setting '--show-tasks=error --log-level=warning' prints tasks that " +
              "have a log with at least error level and for each such a task all logs with at " +
              "least warning level.",
          "",
          "Available task matchers:",
          "  all              - all tasks",
          "  default          - shortcut for '(user&call)|info'",
          "  none             - no tasks",
          "",
          "  f, fatal         - contains a log with fatal level",
          "  e, error         - contains a log with at least error level",
          "  w, warning       - contains a log with at least warning level",
          "  i, info          - contains any log",
          "",
          "  u, user          - evaluates expression from user module",
          "  s, slib          - evaluates expression from smooth standard library module",
          "",
          "  c, call          - evaluates function call",
          "  conv, conversion - evaluates automatic conversion",
          "  l, literal       - evaluates compile time literal"
      }
  )
  TaskMatcher showTasks;

  public static class ShowTasksConverter implements ITypeConverter<TaskMatcher> {
    @Override
    public TaskMatcher convert(String value) {
      return createMatcher(value);
    }
  }

  @Parameters(
      paramLabel = "<function>",
      arity = "1..*",
      description = "function(s) which results are saved as artifacts")
  List<String> functions;

  @Override
  protected Integer executeCommand(Path projectDir) {
    return createInjector(projectDir, installationDir(), out(), logLevel, showTasks)
        .getInstance(BuildRunner.class)
        .run(functions);
  }
}
