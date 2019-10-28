package org.smoothbuild.cli;

import static com.google.common.base.Strings.padEnd;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.cli.Commands.BUILD;
import static org.smoothbuild.cli.Commands.CLEAN;
import static org.smoothbuild.cli.Commands.DAG;
import static org.smoothbuild.cli.Commands.HELP;
import static org.smoothbuild.cli.Commands.LIST;

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
      switch (args[1]) {
        case BUILD:
          console.println(buildDescription());
          break;
        case CLEAN:
          console.println(cleanDescription());
          break;
        case DAG:
          console.println(dagDescription());
          break;
        case HELP:
          console.println(helpDescription());
          break;
        case LIST:
          console.println(listDescription());
          break;
        case Commands.VERSION:
          console.print(versionDescription());
          break;
        default:
          console.println("smooth: unknown '" + args[1] + "' command. See 'smooth help'.");
          return EXIT_CODE_ERROR;
      }
    }
    return EXIT_CODE_SUCCESS;
  }

  private static String generalHelp() {
    StringBuilder builder = new StringBuilder();
    builder.append("usage: smooth <command> <arg>...\n");
    builder.append("\n");
    builder.append("All available commands are:\n");
    append(builder, "build", buildShortDescription());
    append(builder, "clean", cleanShortDescription());
    append(builder, "help", helpShortDescription());
    append(builder, "list", listShortDescription());
    append(builder, "version", versionShortDescription());
    return builder.toString();
  }

  private static void append(StringBuilder builder, String name,
      String description) {
    builder.append("  ");
    builder.append(padEnd(name, 8, ' '));
    builder.append(description);
    builder.append("\n");
  }

  private static String buildDescription() {
    return "usage: smooth build <function>...\n"
        + "\n"
        + buildShortDescription() + "\n"
        + "\n"
        + "  <function>  function which execution result is saved as artifact";
  }

  private static String cleanDescription() {
    return "usage: smooth clean\n"
        + "\n"
        + cleanShortDescription();
  }

  private static String dagDescription() {
    return "usage: smooth dag <function>...\n"
        + "\n"
        + dagShortDescription();
  }

  private static String helpDescription() {
    return "usage: smooth help <command>\n"
        + "\n"
        + helpShortDescription() + "\n"
        + "\n"
        + "arguments:\n"
        + "  <command>  command for which help is printed";
  }

  private static String listDescription() {
    return "usage: smooth list\n"
        + "\n"
        + listShortDescription() + "\n"
        + "\n";
  }

  private static String versionDescription() {
    return "usage: smooth version\n"
        + "\n"
        + versionShortDescription() + "\n"
        + "\n";
  }

  private static String buildShortDescription() {
    return "Build artifact(s) by running specified function(s)";
  }

  private static String cleanShortDescription() {
    return "Remove all cached objects and artifacts calculated during previous builds";
  }

  private static String dagShortDescription() {
    return "Prints execution DAG (directed acyclic graph) of for given function(s)";
  }

  private static String helpShortDescription() {
    return "Print help about given command";
  }

  private static String listShortDescription() {
    return "Print arg-less user defined functions";
  }

  private static String versionShortDescription() {
    return "Print smooth build version number";
  }
}
