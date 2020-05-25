package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.base.CreateInjector.createInjector;

import org.smoothbuild.cli.base.ExclusiveCommand;
import org.smoothbuild.exec.run.CleanRunner;

import picocli.CommandLine.Command;

@Command(
    name = CleanCommand.NAME,
    description = "Remove all cached objects and artifacts calculated during all previous builds"
)
public class CleanCommand extends ExclusiveCommand {
  public static final String NAME = "clean";

  @Override
  protected Integer invokeCall() {
    return createInjector(out(), logLevel)
        .getInstance(CleanRunner.class)
        .run();
  }
}
