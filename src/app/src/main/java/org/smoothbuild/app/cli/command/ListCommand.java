package org.smoothbuild.app.cli.command;

import static org.smoothbuild.app.cli.base.RunStepExecutor.runStepExecutor;
import static org.smoothbuild.app.run.CreateInjector.createInjector;
import static org.smoothbuild.common.step.Step.stepFactory;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import java.nio.file.Path;
import org.smoothbuild.app.cli.base.ProjectCommand;
import org.smoothbuild.app.run.ListStepFactory;
import picocli.CommandLine.Command;

@Command(
    name = ListCommand.NAME,
    description = "Print user defined values that can be evaluated and stored as artifact.")
public class ListCommand extends ProjectCommand {
  public static final String NAME = "list";

  @Override
  protected Integer executeCommand(Path projectDir) {
    var injector = createInjector(projectDir, out(), logLevel);
    var step = stepFactory(new ListStepFactory());
    var argument = tuple();
    return runStepExecutor(injector, step, argument);
  }
}
