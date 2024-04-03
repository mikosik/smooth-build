package org.smoothbuild.app.cli.command;

import static org.smoothbuild.app.cli.base.ExecuteDag.executeDagWithInitializables;
import static org.smoothbuild.app.run.CreateInjector.createInjector;
import static org.smoothbuild.app.run.ListEvaluablesDag.listEvaluablesDag;

import java.nio.file.Path;
import org.smoothbuild.app.cli.base.ProjectCommand;
import picocli.CommandLine.Command;

@Command(
    name = ListCommand.NAME,
    description = "Print user defined values that can be evaluated and stored as artifact.")
public class ListCommand extends ProjectCommand {
  public static final String NAME = "list";

  @Override
  protected Integer executeCommand(Path projectDir) {
    var injector = createInjector(projectDir, out(), logLevel);
    return executeDagWithInitializables(injector, listEvaluablesDag());
  }
}
