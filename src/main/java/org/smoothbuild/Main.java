package org.smoothbuild;

import static org.smoothbuild.cli.Commands.COMMANDS;

import org.smoothbuild.cli.Command;

public class Main {
  public static void main(String[] args) {
    int exitCode = execute(args);
    System.exit(exitCode);
  }

  private static int execute(String[] args) {
    if (args.length == 0) {
      args = new String[] { "help" };
    }
    String commandName = args[0];
    Command command = COMMANDS.get(commandName);
    if (command == null) {
      System.out.println("smooth: '" + commandName
          + "' is not a smooth command. See 'smooth help'.");
      return 1;
    } else {
      return command.execute(args);
    }
  }
}
