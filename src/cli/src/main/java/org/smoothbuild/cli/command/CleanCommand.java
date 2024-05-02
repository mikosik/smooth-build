package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.run.CreateInjector.createInjector;

import java.nio.file.Path;
import org.smoothbuild.cli.run.Clean;
import picocli.CommandLine.Command;

@Command(
    name = CleanCommand.NAME,
    description = "Remove all cached objects and artifacts calculated during all previous builds.")
public class CleanCommand extends ProjectCommand {
  public static final String NAME = "clean";

  @Override
  protected Integer executeCommand(Path projectDir) {
    var injector = createInjector(projectDir, out(), logLevel);
    return injector.getInstance(CommandRunner.class).run(Clean.class);
  }
}
