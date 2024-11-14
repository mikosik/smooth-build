package org.smoothbuild.cli.command.version;

import static org.smoothbuild.common.log.base.Label.label;

import java.util.concurrent.Callable;
import org.smoothbuild.cli.command.base.CommandRunner;
import org.smoothbuild.cli.command.base.CreateInjector;
import org.smoothbuild.cli.command.base.LoggingCommand;
import org.smoothbuild.common.log.base.Label;
import picocli.CommandLine.Command;

@Command(name = VersionCommand.NAME, description = "Print version information.")
public class VersionCommand extends LoggingCommand implements Callable<Integer> {
  public static final String NAME = "version";
  public static final Label LABEL = label("cli:version");

  @Override
  public Integer call() {
    var injector = CreateInjector.createInjector(out());
    return injector.getInstance(CommandRunner.class).run(s -> s.submit(Version.class));
  }
}
