package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.base.CreateInjector.createInjector;

import java.nio.file.Path;

import org.smoothbuild.cli.base.ExclusiveCommand;
import org.smoothbuild.run.ListRunner;

import picocli.CommandLine.Command;

@Command(
    name = ListCommand.NAME,
    description = "Print arg-less user defined functions"
)
public class ListCommand extends ExclusiveCommand {
  public static final String NAME = "list";

  @Override
  protected Integer executeCommand(Path projectDir) {
    return createInjector(projectDir, installationDir(), out(), logLevel)
        .getInstance(ListRunner.class)
        .run();
  }
}
