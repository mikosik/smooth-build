package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.base.CommandHelper.runCommand;

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
    return runCommand(injector -> injector.getInstance(ListRunner.class).run());
  }
}
