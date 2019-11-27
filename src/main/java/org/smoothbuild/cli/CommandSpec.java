package org.smoothbuild.cli;

import static java.util.Locale.ROOT;

public abstract class CommandSpec {
  private final Class<? extends Command> commandClass;

  public CommandSpec(Class<? extends Command> executorClass) {
    this.commandClass = executorClass;
  }

  public String name() {
    return commandClass.getSimpleName().toLowerCase(ROOT);
  }

  public Class<? extends Command> commandClass() {
    return commandClass;
  }

  public abstract String description();

  public abstract String shortDescription();
}
