package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.base.CreateInjector.createInjector;
import static org.smoothbuild.cli.base.RunExclusively.runExclusively;

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
    return runExclusively(this::cleanCommand);
  }

  private int cleanCommand() {
    return createInjector().getInstance(CleanRunner.class).run();
  }
}
