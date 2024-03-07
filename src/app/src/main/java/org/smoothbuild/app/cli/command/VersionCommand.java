package org.smoothbuild.app.cli.command;

import static org.smoothbuild.app.cli.base.RunStepExecutor.runStepExecutor;
import static org.smoothbuild.app.run.CreateInjector.createInjector;
import static org.smoothbuild.common.step.Step.tryStep;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import java.util.concurrent.Callable;
import org.smoothbuild.app.cli.base.LoggingCommand;
import org.smoothbuild.app.run.Version;
import picocli.CommandLine.Command;

@Command(name = VersionCommand.NAME, description = "Print version information.")
public class VersionCommand extends LoggingCommand implements Callable<Integer> {
  public static final String NAME = "version";

  @Override
  public Integer call() {
    var injector = createInjector(out());
    var step = tryStep(Version.class);
    var argument = tuple();
    return runStepExecutor(injector, step, argument);
  }
}
