package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.base.CreateInjector.createInjector;
import static org.smoothbuild.cli.taskmatcher.MatcherCreator.createMatcher;

import java.nio.file.Path;
import java.util.List;

import org.smoothbuild.cli.base.ExclusiveCommand;
import org.smoothbuild.cli.taskmatcher.TaskMatcher;
import org.smoothbuild.run.BuildRunner;

import picocli.CommandLine.Command;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = BuildCommand.NAME,
    description = "Evaluate specified value(s) and store them as artifact(s) "
)
public class BuildCommand extends ExclusiveCommand {
  public static final String NAME = "build";

  @Option(
      names = {"--show-tasks", "-s"},
      defaultValue = "default",
      paramLabel = "<filter>",
      converter = ShowTasksConverter.class,
      description =
          """
          Show executed build tasks that match filter.
          
          Filter is a boolean expression made up of matchers (listed below), \
          boolean operators '&', '|', grouping brackets '(', ')'.
          Default value is 'info|(user&call)'
          
          For each matched tasks its name and properties are printed together with logs that \
          match filter specified with --log-level option. \
          Note that you can filter tasks by one log level and its logs by other level. \
          For example setting '--show-tasks=error --log-level=warning' prints tasks that \
          have a log with at least error level and for each such a task all logs with at \
          least warning level.
          
          Available task matchers:
            a, all             - all tasks
            d, default         - shortcut for 'info|(user&call)'
            n, none            - no tasks
          
            f, fatal           - contains a log with fatal level
            e, error           - contains a log with at least error level
            w, warning         - contains a log with at least warning level
            i, info            - contains any log
          
            p, prj, project    - evaluates expression from project module
            sdk                - evaluates expression from smooth SDK module
          
            c, call            - evaluates function call
            b, combine         - evaluates tuple creation (combined elements)
            t, const           - evaluates compile time constant
            r, convert         - evaluates conversion of value to compatible type
            v, invoke          - evaluates native implementation
            o, order           - evaluates array literal (ordered elements)
            k, pick            - evaluates reading array element
            s, select          - evaluates field selection
          """
  )
  TaskMatcher showTasks;

  public static class ShowTasksConverter implements ITypeConverter<TaskMatcher> {
    @Override
    public TaskMatcher convert(String value) {
      return createMatcher(value);
    }
  }

  @Parameters(
      paramLabel = "<value>",
      arity = "1..*",
      description = "value(s) to evaluate and store as artifact(s)")
  List<String> values;

  @Override
  protected Integer executeCommand(Path projectDir) {
    return createInjector(projectDir, installationDir(), out(), logLevel, showTasks)
        .getInstance(BuildRunner.class)
        .run(values);
  }
}
