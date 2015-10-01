package org.smoothbuild.cli.spec;

import org.smoothbuild.cli.handle.Clean;
import org.smoothbuild.cli.handle.Handler;
import org.smoothbuild.cli.handle.Help;

public enum Command {
  CLEAN(new CleanSpec(), Clean.class),
  HELP(new HelpSpec(), Help.class);

  private final CommandSpec commandSpec;
  private final Class<? extends Handler> handler;

  private Command(CommandSpec commandSpec, Class<? extends Handler> handler) {
    this.commandSpec = commandSpec;
    this.handler = handler;
  }

  public CommandSpec commandSpec() {
    return commandSpec;
  }

  public Class<? extends Handler> handler() {
    return handler;
  }

  public String commandName() {
    return name().toLowerCase();
  }

  public static Command forName(String name) {
    String nameUppercased = name.toUpperCase();
    for (Command command : values()) {
      if (command.name().equals(nameUppercased)) {
        return command;
      }
    }
    return null;
  }
}
