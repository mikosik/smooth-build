package org.smoothbuild.systemtest;

import static com.google.common.collect.ObjectArrays.concat;
import static java.util.Objects.requireNonNull;

import org.smoothbuild.cli.command.build.BuildCommand;
import org.smoothbuild.cli.command.clean.CleanCommand;
import org.smoothbuild.cli.command.list.ListCommand;
import org.smoothbuild.cli.command.version.VersionCommand;

public class CommandWithArgs {
  private final String command;
  private final String[] args;

  public CommandWithArgs(String command, String... args) {
    this.command = requireNonNull(command);
    this.args = args;
  }

  public static CommandWithArgs buildCommand(String... args) {
    return new CommandWithArgs(BuildCommand.NAME, args);
  }

  public static CommandWithArgs cleanCommand(String... args) {
    return new CommandWithArgs(CleanCommand.NAME, args);
  }

  public static CommandWithArgs helpCommand(String... args) {
    return new CommandWithArgs("help", args);
  }

  public static CommandWithArgs listCommand(String... args) {
    return new CommandWithArgs(ListCommand.NAME, args);
  }

  public static CommandWithArgs versionCommand(String... args) {
    return new CommandWithArgs(VersionCommand.NAME, args);
  }

  public String[] commandPlusArgs() {
    return commandPlus(args);
  }

  public String[] commandPlusArgsPlus(String... additionalArgs) {
    String[] allArgs = concat(additionalArgs, args, String.class);
    return commandPlus(allArgs);
  }

  private String[] commandPlus(String[] allArgs) {
    if (command.isBlank()) {
      return allArgs;
    } else {
      return concat(command, allArgs);
    }
  }
}
