package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.base.CreateInjector.createInjector;
import static org.smoothbuild.cli.base.RunExclusively.runExclusively;

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
    return runExclusively(this::listCommand);
  }

  private int listCommand() {
    return createInjector(logLevel)
        .getInstance(ListRunner.class)
        .run();
  }
}
