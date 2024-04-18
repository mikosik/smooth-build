package org.smoothbuild.cli.command;

import org.smoothbuild.common.log.base.Level;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command
public class LoggingCommand extends ACommand {
  @Option(
      names = {"--log-level", "-l"},
      defaultValue = "info",
      paramLabel = "<level>",
      converter = LogLevelConverter.class,
      description =
          """
              Show logs with specified level or above.

              Available levels:
                f, fatal   - show FATAL logs
                e, error   - show FATAL, ERROR logs
                w, warning - show FATAL, ERROR, WARNING logs
                i, info    - show FATAL, ERROR, WARNING, INFO logs
              """)
  public Level logLevel;
}
