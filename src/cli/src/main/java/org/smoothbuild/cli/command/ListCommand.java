package org.smoothbuild.cli.command;

import java.nio.file.Path;
import org.smoothbuild.cli.run.CreateInjector;
import org.smoothbuild.cli.run.ListEvaluables;
import org.smoothbuild.common.plan.Plan;
import org.smoothbuild.common.tuple.Tuple0;
import picocli.CommandLine.Command;

@Command(
    name = ListCommand.NAME,
    description = "Print user defined values that can be evaluated and stored as artifact.")
public class ListCommand extends ProjectCommand {
  public static final String NAME = "list";

  @Override
  protected Integer executeCommand(Path projectDir) {
    var injector = CreateInjector.createInjector(projectDir, out(), logLevel);
    Plan<Tuple0> plan = Plan.task0(ListEvaluables.class);
    return injector.getInstance(CommandExecutor.class).execute(plan);
  }
}
