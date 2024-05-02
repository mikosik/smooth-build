package org.smoothbuild.cli.command;

import java.util.concurrent.Callable;
import org.smoothbuild.cli.run.CreateInjector;
import org.smoothbuild.cli.run.Version;
import picocli.CommandLine.Command;

@Command(name = VersionCommand.NAME, description = "Print version information.")
public class VersionCommand extends LoggingCommand implements Callable<Integer> {
  public static final String NAME = "version";

  @Override
  public Integer call() {
    var injector = CreateInjector.createInjector(out());
    return injector.getInstance(CommandRunner.class).run(Version.class);
  }
}
