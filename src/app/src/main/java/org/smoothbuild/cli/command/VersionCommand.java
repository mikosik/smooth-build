package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.base.RunStepExecutor.runStepExecutor;
import static org.smoothbuild.run.CreateInjector.createInjector;
import static org.smoothbuild.run.step.Step.step;

import java.util.concurrent.Callable;

import org.smoothbuild.cli.base.LoggingCommand;
import org.smoothbuild.run.Version;

import io.vavr.Tuple;
import picocli.CommandLine.Command;

@Command(
    name = VersionCommand.NAME,
    description = "Print version information."
)
public class VersionCommand extends LoggingCommand implements Callable<Integer> {
  public static final String NAME = "version";

  @Override
  public Integer call() {
    var injector = createInjector(out());
    var step = step(Version.class);
    var argument = Tuple.empty();
    return runStepExecutor(injector, step, argument);
  }
}
