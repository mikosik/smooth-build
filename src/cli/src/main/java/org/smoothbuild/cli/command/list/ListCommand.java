package org.smoothbuild.cli.command.list;

import static org.smoothbuild.cli.command.base.CreateInjector.createAliasPathMap;
import static org.smoothbuild.common.log.base.Label.label;

import java.nio.file.Path;
import org.smoothbuild.cli.command.base.ProjectCommand;
import org.smoothbuild.common.log.base.Label;
import picocli.CommandLine.Command;

@Command(
    name = ListCommand.NAME,
    description = "Print user defined values that can be evaluated and stored as artifact.")
public class ListCommand extends ProjectCommand {
  public static final String NAME = "list";
  public static final Label LABEL = label(":cli:list");

  @Override
  protected Integer executeCommand(Path projectDir) {
    var commandRunnerFactory = DaggerListCommandRunnerFactory.builder()
        .aliasPathMap(createAliasPathMap(projectDir))
        .out(out())
        .logLevel(filterLogs)
        .filterTasks(report -> true)
        .filterTraces(report -> true)
        .build();
    return commandRunnerFactory.listCommandRunner().run();
  }
}
