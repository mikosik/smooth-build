package org.smoothbuild.app.cli.command;

import static org.smoothbuild.app.cli.base.ExecuteDag.executeDag;
import static org.smoothbuild.app.run.CreateInjector.createInjector;
import static org.smoothbuild.app.run.eval.report.MatcherCreator.createMatcher;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.dag.Dag.apply0;
import static org.smoothbuild.common.dag.Dag.apply1;
import static org.smoothbuild.common.dag.Dag.chain;
import static org.smoothbuild.evaluator.SmoothEvaluationDag.smoothEvaluationDag;

import java.nio.file.Path;
import org.smoothbuild.app.cli.base.ProjectCommand;
import org.smoothbuild.app.layout.Layout;
import org.smoothbuild.app.run.RemoveArtifacts;
import org.smoothbuild.app.run.eval.SaveArtifacts;
import org.smoothbuild.common.log.report.ReportMatcher;
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
            const              - evaluates compile time constant
            invoke             - evaluates native function call
            order              - evaluates array creation
            pick               - evaluates array element picking
            select             - evaluates field selection
          """)
  ReportMatcher showTasks;

  public static class ShowTasksConverter implements ITypeConverter<ReportMatcher> {
    @Override
    public ReportMatcher convert(String value) {
      return createMatcher(value);
    }
  }

  @Parameters(
      paramLabel = "<value>",
      arity = "1..*",
      description = "value(s) to evaluate and store as artifact(s)")
  java.util.List<String> values;

  @Override
  protected Integer executeCommand(Path projectDir) {
    var removedArtifacts = apply0(RemoveArtifacts.class);
    var evaluated = smoothEvaluationDag(Layout.MODULES, listOfAll(values));
    var artifacts = chain(removedArtifacts, evaluated);
    var dag = apply1(SaveArtifacts.class, artifacts);

    var injector = createInjector(projectDir, out(), logLevel, showTasks);
    return executeDag(injector, dag);
  }
}
