package org.smoothbuild.cli.base;

import org.smoothbuild.cli.console.Level;
import org.smoothbuild.cli.console.LogLevelConverter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command
public class LoggingCommand extends FormattedHeadings {
  @Option(
      names = { "--log-level", "-l" },
      defaultValue = "info",
      paramLabel = "<level>",
      converter = LogLevelConverter.class,
      description =
          "Show logs with specified level or above.\n" +
          "\n" +
          "Available levels:\n" +
          "  f, fatal   - show FATAL logs\n" +
          "  e, error   - show FATAL, ERROR logs\n" +
          "  w, warning - show FATAL, ERROR, WARNING logs\n" +
          "  i, info    - show FATAL, ERROR, WARNING, INFO logs\n"
  )
  public Level logLevel;
}
