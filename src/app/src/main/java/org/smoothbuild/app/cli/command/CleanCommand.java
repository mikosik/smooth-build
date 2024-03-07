package org.smoothbuild.app.cli.command;

import static org.smoothbuild.app.cli.base.RunStepExecutor.runStepExecutor;
import static org.smoothbuild.app.run.CreateInjector.createInjector;
import static org.smoothbuild.common.step.Step.tryStep;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import java.nio.file.Path;
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
    var step = tryStep(Clean.class);
    var argument = tuple();
    return runStepExecutor(injector, step, argument);
  }
}
