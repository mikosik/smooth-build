package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.base.CreateInjector.createInjector;

import java.nio.file.Path;

import org.smoothbuild.cli.base.ProjectCommand;
import org.smoothbuild.run.ListRunner;

import picocli.CommandLine.Command;

@Command(
    name = ListCommand.NAME,
    description = "Print user defined values that can be evaluated and stored as artifact."
)
public class ListCommand extends ProjectCommand {
  public static final String NAME = "list";

  @Override
  protected Integer executeCommand(Path projectDir) {
    return createInjector(projectDir, out(), logLevel)
        .getInstance(ListRunner.class)
        .run();
  }
}
