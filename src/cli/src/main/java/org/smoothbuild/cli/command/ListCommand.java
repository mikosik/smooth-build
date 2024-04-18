package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.run.ListEvaluablesPlan.listEvaluablesPlan;

import java.nio.file.Path;
import org.smoothbuild.cli.run.CreateInjector;
import org.smoothbuild.common.plan.Plan;
import picocli.CommandLine.Command;

@Command(
    name = ListCommand.NAME,
    description = "Print user defined values that can be evaluated and stored as artifact.")
public class ListCommand extends ProjectCommand {
  public static final String NAME = "list";

  @Override
  protected Integer executeCommand(Path projectDir) {
    var injector = CreateInjector.createInjector(projectDir, out(), logLevel);
    Plan<Void> plan = listEvaluablesPlan();
    return injector.getInstance(CommandExecutor.class).execute(plan);
  }
}
