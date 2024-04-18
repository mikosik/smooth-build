package org.smoothbuild.app.cli.command;

import static org.smoothbuild.app.run.CreateInjector.createInjector;
import static org.smoothbuild.common.plan.Plan.apply0;

import java.nio.file.Path;
import org.smoothbuild.app.cli.base.CommandExecutor;
import org.smoothbuild.app.cli.base.ProjectCommand;
import org.smoothbuild.app.run.Clean;
import picocli.CommandLine.Command;

@Command(
    name = CleanCommand.NAME,
    description = "Remove all cached objects and artifacts calculated during all previous builds.")
public class CleanCommand extends ProjectCommand {
  public static final String NAME = "clean";

  @Override
  protected Integer executeCommand(Path projectDir) {
    var injector = createInjector(projectDir, out(), logLevel);
    var plan = apply0(Clean.class);
    return injector.getInstance(CommandExecutor.class).execute(plan);
  }
}
