package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.base.RunStepExecutor.runStepExecutor;
import static org.smoothbuild.run.CreateInjector.createInjector;
import static org.smoothbuild.run.step.Step.stepFactory;

import java.nio.file.Path;

import org.smoothbuild.cli.base.ProjectCommand;
import org.smoothbuild.run.ListStepFactory;

import io.vavr.Tuple;
import picocli.CommandLine.Command;

@Command(
    name = ListCommand.NAME,
    description = "Print user defined values that can be evaluated and stored as artifact."
)
public class ListCommand extends ProjectCommand {
  public static final String NAME = "list";

  @Override
  protected Integer executeCommand(Path projectDir) {
    var injector = createInjector(projectDir, out(), logLevel);
    var step = stepFactory(new ListStepFactory());
    var argument = Tuple.empty();
    return runStepExecutor(injector, step, argument);
  }
}
