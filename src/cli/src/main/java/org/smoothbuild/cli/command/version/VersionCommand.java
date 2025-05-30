package org.smoothbuild.cli.command.version;

import static org.smoothbuild.cli.command.base.CreateInjector.createAliasPathMap;
import static org.smoothbuild.common.log.base.Label.label;

import java.util.concurrent.Callable;
import org.smoothbuild.cli.command.base.LoggingCommand;
import org.smoothbuild.common.log.base.Label;
import picocli.CommandLine.Command;

@Command(name = VersionCommand.NAME, description = "Print version information.")
public class VersionCommand extends LoggingCommand implements Callable<Integer> {
  public static final String NAME = "version";
  public static final Label LABEL = label(":cli:version");

  @Override
  public Integer call() {
    var commandRunnerFactory = DaggerVersionCommandRunnerFactory.builder()
        .aliasPathMap(createAliasPathMap())
        .out(out())
        .logLevel(filterLogs)
        .filterTasks(report -> true)
        .filterTraces(report -> true)
        .build();
    return commandRunnerFactory.versionCommandRunner().run();
  }
}
