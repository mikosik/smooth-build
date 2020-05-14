package org.smoothbuild.cli.command;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.cli.base.CommandHelper.runCommand;
import static org.smoothbuild.exec.run.Locker.tryAcquireLock;

import java.util.concurrent.Callable;

import org.smoothbuild.cli.base.LoggingCommand;
import org.smoothbuild.exec.run.CleanRunner;

import picocli.CommandLine.Command;

@Command(
    name = CleanCommand.NAME,
    description = "Remove all cached objects and artifacts calculated during all previous builds"
)
public class CleanCommand extends LoggingCommand implements Callable<Integer> {
  public static final String NAME = "clean";

  @Override
  public Integer call() {
    if (!tryAcquireLock()) {
      return EXIT_CODE_ERROR;
    }
    return runCommand(injector -> injector.getInstance(CleanRunner.class).run());
  }
}
