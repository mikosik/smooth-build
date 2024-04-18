package org.smoothbuild.cli.command;

import static org.smoothbuild.common.plan.Plan.apply0;

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
    var version = apply0(Version.class);
    return injector.getInstance(CommandExecutor.class).execute(version);
  }
}
