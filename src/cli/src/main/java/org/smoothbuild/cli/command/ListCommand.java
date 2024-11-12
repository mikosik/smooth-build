package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.run.CreateInjector.createInjector;

import java.nio.file.Path;
import org.smoothbuild.cli.run.ScheduleList;
import picocli.CommandLine.Command;

@Command(
    name = ListCommand.NAME,
    description = "Print user defined values that can be evaluated and stored as artifact.")
public class ListCommand extends ProjectCommand {
  public static final String NAME = "list";

  @Override
  protected Integer executeCommand(Path projectDir) {
    var injector = createInjector(projectDir, out(), logLevel);
    return injector.getInstance(CommandRunner.class).run(s -> s.submit(ScheduleList.class));
  }
}
