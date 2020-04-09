package org.smoothbuild.cli;

import static org.smoothbuild.cli.CommandHelper.runCommand;

import java.util.concurrent.Callable;

import picocli.CommandLine.Command;

@Command(
    name = CleanCommand.NAME,
    description = "Remove all cached objects and artifacts calculated during all previous builds"
)
public class CleanCommand extends StandardOptions implements Callable<Integer> {
  public static final String NAME = "clean";

  @Override
  public Integer call() {
    return runCommand(injector -> injector.getInstance(Clean.class).run());
  }
}
