package org.smoothbuild.cli.command;

import jakarta.inject.Inject;
import java.nio.file.Path;
import org.smoothbuild.cli.run.CreateInjector;
import org.smoothbuild.cli.run.ListEvaluables;
import org.smoothbuild.common.task.TaskExecutor;
import picocli.CommandLine.Command;

@Command(
    name = ListCommand.NAME,
    description = "Print user defined values that can be evaluated and stored as artifact.")
public class ListCommand extends ProjectCommand {
  public static final String NAME = "list";

  @Override
  protected Integer executeCommand(Path projectDir) {
    var injector = CreateInjector.createInjector(projectDir, out(), logLevel);
    return injector.getInstance(ListCommandRunner.class).run();
  }

  public static class ListCommandRunner {
    private final TaskExecutor taskExecutor;
    private final CommandCompleter commandCompleter;

    @Inject
    public ListCommandRunner(TaskExecutor taskExecutor, CommandCompleter commandCompleter) {
      this.taskExecutor = taskExecutor;
      this.commandCompleter = commandCompleter;
    }

    public int run() {
      taskExecutor.submit(ListEvaluables.class);
      return commandCompleter.waitForCompletion();
    }
  }
}
