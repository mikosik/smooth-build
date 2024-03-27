package org.smoothbuild.systemtest.cli.command;

import org.junit.jupiter.api.Test;
import org.smoothbuild.app.cli.command.BuildCommand;
import org.smoothbuild.app.cli.command.CleanCommand;
import org.smoothbuild.app.cli.command.ListCommand;
import org.smoothbuild.app.cli.command.VersionCommand;
import org.smoothbuild.systemtest.SystemTestCase;

public class HelpCommandTest extends SystemTestCase {
  @Test
  public void help_command_prints_general_help() {
    runSmoothHelp();
    assertFinishedWithSuccess();
    assertSystemOutContains(
        """
        Usage:
        smooth COMMAND

        Description:
        smooth-build is a build tool with strongly and statically typed, purely
        functional language. It features fine-grained, aggressive caching that will
        make sure no computation happens twice on the same machine, decreasing build
        times significantly.
        More info at https://github.com/mikosik/smooth-build/blob/master/doc/tutorial.md

        Commands:
          build    Evaluate specified value(s) and store them as artifact(s).
          clean    Remove all cached objects and artifacts calculated during all
                     previous builds.
          help     Display help information about the specified command.
          list     Print user defined values that can be evaluated and stored as
                     artifact.
          version  Print version information.
        """);
  }

  @Test
  public void help_build() {
    runSmoothHelp(BuildCommand.NAME);
    assertFinishedWithSuccess();
    assertSystemOutContains(
        """
        Usage:
        smooth build [-l=<level>] [-s=<filter>] <value>...

        Description:
        Evaluate specified value(s) and store them as artifact(s).

        Parameters:
              <value>...            value(s) to evaluate and store as artifact(s)

        Options:
          -l, --log-level=<level>   Show logs with specified level or above.

                                    Available levels:
                                      f, fatal   - show FATAL logs
                                      e, error   - show FATAL, ERROR logs
                                      w, warning - show FATAL, ERROR, WARNING logs
                                      i, info    - show FATAL, ERROR, WARNING, INFO logs

          -s, --show-tasks=<filter> Show executed build tasks that match filter.

                                    Filter is a boolean expression made up of matchers
                                      (listed below), boolean operators '&', '|',
                                      grouping brackets '(', ')'.
                                    Default value is 'info|call'

                                    For each matched tasks its name and properties are
                                      printed together with logs that match filter
                                      specified with --log-level option. Note that you
                                      can filter tasks by one log level and its logs by
                                      other level. For example setting
                                      '--show-tasks=error --log-level=warning' prints
                                      tasks that have a log with at least error level
                                      and for each such a task all logs with at least
                                      warning level.

                                    Available task matchers:
                                      a, all             - all tasks
                                      d, default         - shortcut for 'info|call'
                                      n, none            - no tasks

                                      lf, fatal          - contains a log with fatal
                                      level
                                      le, error          - contains a log with at least
                                      error level
                                      lw, warning        - contains a log with at least
                                      warning level
                                      li, info           - contains any log

                                      combine            - evaluates tuple creation
                                      const              - evaluates compile time
                                      constant
                                      invoke             - evaluates native function
                                      call
                                      order              - evaluates array creation
                                      pick               - evaluates array element
                                      picking
                                      select             - evaluates field selection
           """);
  }

  @Test
  public void help_clean() {
    runSmoothHelp(CleanCommand.NAME);
    assertFinishedWithSuccess();
    assertSystemOutContains(
        """
        Usage:
        smooth clean [-l=<level>]

        Description:
        Remove all cached objects and artifacts calculated during all previous builds.

        Options:
          -l, --log-level=<level>   Show logs with specified level or above.

                                    Available levels:
                                      f, fatal   - show FATAL logs
                                      e, error   - show FATAL, ERROR logs
                                      w, warning - show FATAL, ERROR, WARNING logs
                                      i, info    - show FATAL, ERROR, WARNING, INFO logs

        """);
  }

  @Test
  public void help_list() {
    runSmoothHelp(ListCommand.NAME);
    assertFinishedWithSuccess();
    assertSystemOutContains(
        """
        Usage:
        smooth list [-l=<level>]

        Description:
        Print user defined values that can be evaluated and stored as artifact.

        Options:
          -l, --log-level=<level>   Show logs with specified level or above.

                                    Available levels:
                                      f, fatal   - show FATAL logs
                                      e, error   - show FATAL, ERROR logs
                                      w, warning - show FATAL, ERROR, WARNING logs
                                      i, info    - show FATAL, ERROR, WARNING, INFO logs

        """);
  }

  @Test
  public void help_version() {
    runSmoothHelp(VersionCommand.NAME);
    assertFinishedWithSuccess();
    assertSystemOutContains(
        """
        Usage:
        smooth version [-l=<level>]

        Description:
        Print version information.

        Options:
          -l, --log-level=<level>   Show logs with specified level or above.

                                    Available levels:
                                      f, fatal   - show FATAL logs
                                      e, error   - show FATAL, ERROR logs
                                      w, warning - show FATAL, ERROR, WARNING logs
                                      i, info    - show FATAL, ERROR, WARNING, INFO logs

        """);
  }
}
