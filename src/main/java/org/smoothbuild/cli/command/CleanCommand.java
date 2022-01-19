package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.base.CreateInjector.createInjector;

import java.nio.file.Path;

import org.smoothbuild.cli.base.ProjectCommand;
import org.smoothbuild.run.CleanRunner;

import picocli.CommandLine.Command;

@Command(
    name = CleanCommand.NAME,
    description = "Remove all cached objects and artifacts calculated during all previous builds"
)
public class CleanCommand extends ProjectCommand {
  public static final String NAME = "clean";

  @Override
  protected Integer executeCommand(Path projectDir) {
    return createInjector(projectDir, installationDir(), out(), logLevel)
        .getInstance(CleanRunner.class)
        .run();
  }
}
