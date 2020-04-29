package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.base.CommandHelper.runCommand;

import java.util.concurrent.Callable;

import org.smoothbuild.cli.base.LoggingCommand;
import org.smoothbuild.exec.run.VersionRunner;

import picocli.CommandLine.Command;

@Command(
    name = VersionCommand.NAME,
    description = "Print version information and exit"
)
public class VersionCommand extends LoggingCommand implements Callable<Integer> {
  public static final String NAME = "version";

  @Override
  public Integer call() {
    return runCommand(injector -> injector.getInstance(VersionRunner.class).run());
  }
}
