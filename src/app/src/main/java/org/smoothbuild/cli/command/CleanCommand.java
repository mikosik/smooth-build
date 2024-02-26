package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.base.RunStepExecutor.runStepExecutor;
import static org.smoothbuild.common.step.Step.step;
import static org.smoothbuild.common.tuple.Tuples.tuple;
import static org.smoothbuild.run.CreateInjector.createInjector;

import java.nio.file.Path;
import org.smoothbuild.cli.base.ProjectCommand;
import org.smoothbuild.run.Clean;
import picocli.CommandLine.Command;

@Command(
    name = CleanCommand.NAME,
    description = "Remove all cached objects and artifacts calculated during all previous builds.")
public class CleanCommand extends ProjectCommand {
  public static final String NAME = "clean";

  @Override
  protected Integer executeCommand(Path projectDir) {
    var injector = createInjector(projectDir, out(), logLevel);
    var step = step(Clean.class);
    var argument = tuple();
    return runStepExecutor(injector, step, argument);
  }
}
