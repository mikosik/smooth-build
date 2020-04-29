package org.smoothbuild.cli;

import static org.smoothbuild.cli.CommandHelper.runCommand;

import java.util.concurrent.Callable;

import org.smoothbuild.exec.run.VersionRunner;

import picocli.CommandLine.Command;

@Command(
    name = VersionCommand.NAME,
    description = "Print version information and exit"
)
public class VersionCommand extends StandardOptions implements Callable<Integer> {
  public static final String NAME = "version";

  @Override
  public Integer call() {
    return runCommand(injector -> injector.getInstance(VersionRunner.class).run());
  }
}
