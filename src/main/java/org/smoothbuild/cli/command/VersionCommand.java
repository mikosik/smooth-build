package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.base.CreateInjector.createInjector;

import java.util.concurrent.Callable;

import org.smoothbuild.cli.base.LoggingCommand;
import org.smoothbuild.exec.run.VersionRunner;

import picocli.CommandLine.Command;

@Command(
    name = VersionCommand.NAME,
    description = "Print version information"
)
public class VersionCommand extends LoggingCommand implements Callable<Integer> {
  public static final String NAME = "version";

  @Override
  public Integer call() {
    return createInjector(out(), logLevel)
        .getInstance(VersionRunner.class)
        .run();
  }
}
