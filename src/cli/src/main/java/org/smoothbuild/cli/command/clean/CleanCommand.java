package org.smoothbuild.cli.command.clean;

import static org.smoothbuild.cli.command.base.CreateInjector.createAliasPathMap;
import static org.smoothbuild.common.log.base.Label.label;

import java.nio.file.Path;
import org.smoothbuild.cli.command.base.ProjectCommand;
import org.smoothbuild.common.log.base.Label;
import picocli.CommandLine.Command;

@Command(
    name = CleanCommand.NAME,
    description = "Remove all cached objects and artifacts calculated during all previous builds.")
public class CleanCommand extends ProjectCommand {
  public static final String NAME = "clean";
  public static final Label LABEL = label(":cli:clean");

  @Override
  protected Integer executeCommand(Path projectDir) {
    var commandRunnerFactory = DaggerCleanCommandRunnerFactory.builder()
        .aliasPathMap(createAliasPathMap(projectDir))
        .out(out())
        .logLevel(filterLogs)
        .filterTasks(report -> true)
        .filterTraces(report -> true)
        .build();
    return commandRunnerFactory.cleanRunner().run();
  }
}
