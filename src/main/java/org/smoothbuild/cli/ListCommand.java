package org.smoothbuild.cli;

import static org.smoothbuild.cli.CommandHelper.runCommand;

import java.util.concurrent.Callable;

import org.smoothbuild.exec.run.ListRunner;

import picocli.CommandLine.Command;

@Command(
    name = ListCommand.NAME,
    description = "Print arg-less user defined functions"
)
public class ListCommand extends StandardOptions implements Callable<Integer> {
  public static final String NAME = "list";

  @Override
  public Integer call() {
    return runCommand(injector -> injector.getInstance(ListRunner.class).run());
  }
}
