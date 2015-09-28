package org.smoothbuild.cli;

import static com.google.common.base.Strings.padEnd;
import static org.smoothbuild.cli.Commands.COMMANDS;

import java.util.Map.Entry;

public class HelpCommand implements Command {
  @Override
  public String shortDescription() {
    return "Print help about given command";
  }

  @Override
  public String longDescription() {
    StringBuilder builder = new StringBuilder();
    builder.append("usage: smooth help <command>\n");
    builder.append("\n");
    builder.append(shortDescription() + "\n");
    builder.append("\n");
    builder.append("arguments:\n");
    builder.append("  <command>  command for which help is printed");
    return builder.toString();
  }

  @Override
  public int execute(String[] args) {
    if (1 < args.length) {
      Command command = COMMANDS.get(args[1]);
      if (command == null) {
        System.out.println("smooth: unknown '" + args[1] + "' command. See 'smooth help'.");
        return 1;
      } else {
        System.out.println(command.longDescription());
        return 0;
      }
    } else {
      return generalHelp();
    }
  }

  private int generalHelp() {
    StringBuilder builder = new StringBuilder();
    builder.append("usage: smooth <command> <arg>...\n");
    builder.append("\n");
    builder.append("All available commands are:\n");
    for (Entry<String, Command> entry : COMMANDS.entrySet()) {
      builder.append("  ");
      builder.append(padEnd(entry.getKey(), 8, ' '));
      builder.append(entry.getValue().shortDescription());
      builder.append("\n");
    }

    System.out.print(builder.toString());
    return 0;
  }
}
