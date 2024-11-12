package org.smoothbuild.cli.command;

import jakarta.inject.Inject;
import java.util.function.Function;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.task.Scheduler;

public class CommandRunner {
  private final Scheduler scheduler;
  private final CommandCompleter commandCompleter;

  @Inject
  public CommandRunner(Scheduler scheduler, CommandCompleter commandCompleter) {
    this.scheduler = scheduler;
    this.commandCompleter = commandCompleter;
  }

  public <T extends Maybe<?>> int run(Function<Scheduler, Promise<T>> schedulingFunction) {
    Promise<T> result = schedulingFunction.apply(scheduler);
    return commandCompleter.waitForCompletion(result);
  }
}
