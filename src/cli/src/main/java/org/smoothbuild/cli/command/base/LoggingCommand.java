package org.smoothbuild.cli.command.base;

import org.smoothbuild.common.log.base.Level;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command
public class LoggingCommand extends ACommand {
  @Option(
      names = {"--filter-logs", "-l"},
      defaultValue = "info",
      paramLabel = "<level>",
      converter = FilterLogsConverter.class,
      description =
          """
              Print logs with specified level or above.
              Defaults value is 'info'.

              Available levels:
                f, fatal   - show FATAL logs
                e, error   - show FATAL, ERROR logs
                w, warning - show FATAL, ERROR, WARNING logs
                i, info    - show FATAL, ERROR, WARNING, INFO logs
              """)
  public Level filterLogs;
}
