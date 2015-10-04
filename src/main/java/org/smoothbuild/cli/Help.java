package org.smoothbuild.cli;

import static com.google.common.base.Strings.padEnd;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.cli.Commands.COMMANDS;

import java.util.Map.Entry;

public class Help implements Command {
  @Override
  public int run(String... args) {
    if (1 < args.length) {
      CommandSpec commandSpec = COMMANDS.get(args[1]);
      if (commandSpec == null) {
        System.out.println("smooth: unknown '" + args[1] + "' command. See 'smooth help'.");
        return EXIT_CODE_ERROR;
      } else {
        System.out.println(commandSpec.longDescription());
        return EXIT_CODE_SUCCESS;
      }
    } else {
      return generalHelp();
    }
  }

  private static int generalHelp() {
    StringBuilder builder = new StringBuilder();
    builder.append("usage: smooth <command> <arg>...\n");
    builder.append("\n");
    builder.append("All available commands are:\n");
    for (Entry<String, CommandSpec> entry : COMMANDS.entrySet()) {
      builder.append("  ");
      builder.append(padEnd(entry.getKey(), 8, ' '));
      builder.append(entry.getValue().shortDescription());
      builder.append("\n");
    }

    System.out.print(builder.toString());
    return EXIT_CODE_SUCCESS;
  }
}
