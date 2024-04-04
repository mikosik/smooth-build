package org.smoothbuild.app.cli.command;

import static org.smoothbuild.app.run.CreateInjector.createInjector;
import static org.smoothbuild.common.dag.Dag.apply0;

import java.util.concurrent.Callable;
import org.smoothbuild.app.cli.base.CommandExecutor;
import org.smoothbuild.app.cli.base.LoggingCommand;
import org.smoothbuild.app.run.Version;
import picocli.CommandLine.Command;

@Command(name = VersionCommand.NAME, description = "Print version information.")
public class VersionCommand extends LoggingCommand implements Callable<Integer> {
  public static final String NAME = "version";

  @Override
  public Integer call() {
    var injector = createInjector(out());
    var version = apply0(Version.class);
    return injector.getInstance(CommandExecutor.class).execute(version);
  }
}
