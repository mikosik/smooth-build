package org.smoothbuild.cli.command;

import com.google.inject.Key;
import jakarta.inject.Inject;
import org.smoothbuild.common.task.Task0;
import org.smoothbuild.common.task.TaskExecutor;

public class CommandRunner {
  private final TaskExecutor taskExecutor;
  private final CommandCompleter commandCompleter;

  @Inject
  public CommandRunner(TaskExecutor taskExecutor, CommandCompleter commandCompleter) {
    this.taskExecutor = taskExecutor;
    this.commandCompleter = commandCompleter;
  }

  public int run(Class<? extends Task0<Void>> taskClass) {
    taskExecutor.submit(Key.get(taskClass));
    return commandCompleter.waitForCompletion();
  }
}
