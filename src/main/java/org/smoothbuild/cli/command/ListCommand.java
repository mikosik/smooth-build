package org.smoothbuild.cli.command;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.cli.base.CommandHelper.runCommand;
import static org.smoothbuild.exec.run.Locker.tryAcquireLock;

import java.util.concurrent.Callable;

import org.smoothbuild.cli.base.LoggingCommand;
import org.smoothbuild.exec.run.ListRunner;

import picocli.CommandLine.Command;

@Command(
    name = ListCommand.NAME,
    description = "Print arg-less user defined functions"
)
public class ListCommand extends LoggingCommand implements Callable<Integer> {
  public static final String NAME = "list";

  @Override
  public Integer call() {
    if (!tryAcquireLock()) {
      return EXIT_CODE_ERROR;
    }
    return runCommand(injector -> injector.getInstance(ListRunner.class).run());
  }
}
