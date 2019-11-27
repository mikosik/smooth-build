package org.smoothbuild.cli;

import static com.google.common.base.Strings.padEnd;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.cli.Commands.COMMANDS;
import static org.smoothbuild.cli.Commands.specForCommand;

import java.util.Optional;

import javax.inject.Inject;

public class Help implements Command {
  private final Console console;

  @Inject
  public Help(Console console) {
    this.console = console;
  }

  @Override
  public int run(String... args) {
    if (args.length == 1) {
      console.print(generalHelp());
    } else {
      Optional<CommandSpec> commandSpec = specForCommand(args[1]);
      if (commandSpec.isEmpty()) {
        console.println("smooth: unknown '" + args[1] + "' command. See 'smooth help'.");
        return EXIT_CODE_ERROR;
      } else {
        console.println(commandSpec.get().description());
      }
    }
    return EXIT_CODE_SUCCESS;
  }

  private static String generalHelp() {
    StringBuilder builder = new StringBuilder();
    builder.append("usage: smooth <command> <arg>...\n");
    builder.append("\n");
    builder.append("All available commands are:\n");
    COMMANDS.forEach(c -> append(builder, c.name(), c.shortDescription()));
    return builder.toString();
  }

  private static void append(StringBuilder builder, String name,
      String description) {
    builder.append("  ");
    builder.append(padEnd(name, 8, ' '));
    builder.append(description);
    builder.append("\n");
  }

}
