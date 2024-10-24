package org.smoothbuild.cli.command;

import com.google.inject.Key;
import jakarta.inject.Inject;
import org.smoothbuild.common.task.Scheduler;
import org.smoothbuild.common.task.Task0;

public class CommandRunner {
  private final Scheduler scheduler;
  private final CommandCompleter commandCompleter;

  @Inject
  public CommandRunner(Scheduler scheduler, CommandCompleter commandCompleter) {
    this.scheduler = scheduler;
    this.commandCompleter = commandCompleter;
  }

  public <T> int run(Class<? extends Task0<T>> taskClass) {
    var result = scheduler.submit(Key.get(taskClass));
    return commandCompleter.waitForCompletion(result);
  }
}
