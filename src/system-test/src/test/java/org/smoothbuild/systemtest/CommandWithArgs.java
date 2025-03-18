package org.smoothbuild.systemtest;

import static com.google.common.collect.ObjectArrays.concat;
import static java.util.Objects.requireNonNull;

import org.smoothbuild.cli.command.build.BuildCommand;
import org.smoothbuild.cli.command.clean.CleanCommand;
import org.smoothbuild.cli.command.list.ListCommand;
import org.smoothbuild.cli.command.version.VersionCommand;

public class CommandWithArgs {
  private final String[] command;

  public CommandWithArgs(String... command) {
    this.command = requireNonNull(command);
  }

  public static CommandWithArgs buildCommand(String... args) {
    return new CommandWithArgs(concat(BuildCommand.NAME, args));
  }

  public static CommandWithArgs cleanCommand(String... args) {
    return new CommandWithArgs(concat(CleanCommand.NAME, args));
  }

  public static CommandWithArgs helpCommand(String... args) {
    return new CommandWithArgs(concat("help", args));
  }

  public static CommandWithArgs listCommand(String... args) {
    return new CommandWithArgs(concat(ListCommand.NAME, args));
  }

  public static CommandWithArgs versionCommand(String... args) {
    return new CommandWithArgs(concat(VersionCommand.NAME, args));
  }

  public String[] commandPlusArgs() {
    return command;
  }
}
