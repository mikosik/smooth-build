package org.smoothbuild.acceptance;

import static com.google.common.collect.ObjectArrays.concat;
import static java.util.Objects.requireNonNull;

import org.smoothbuild.cli.command.BuildCommand;
import org.smoothbuild.cli.command.CleanCommand;
import org.smoothbuild.cli.command.ListCommand;
import org.smoothbuild.cli.command.TreeCommand;
import org.smoothbuild.cli.command.VersionCommand;

public class CommandWithArgs {
  private final String command;
  private final String [] arguments;

  public CommandWithArgs(String command, String... arguments) {
    this.command = requireNonNull(command);
    this.arguments = arguments;
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

  public static CommandWithArgs treeCommand(String... args) {
    return new CommandWithArgs(TreeCommand.NAME, args);
  }

  public static CommandWithArgs versionCommand(String... args) {
    return new CommandWithArgs(VersionCommand.NAME, args);
  }

  public String[] commandAndArgs() {
    return concat(command, arguments);
  }
}
