package org.smoothbuild.cli;

import static com.google.inject.Guice.createInjector;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.SMOOTH_HOME_ENV_VARIABLE;
import static org.smoothbuild.util.Strings.unlines;

import java.nio.file.Paths;
import java.util.Optional;

import org.smoothbuild.MainModule;
import org.smoothbuild.SmoothPaths;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;

public class Commands {
  public static final CommandSpec BUILD = buildCommand();
  public static final CommandSpec CLEAN = cleanCommand();
  public static final CommandSpec DAG = dagCommand();
  public static final CommandSpec HELP = helpCommand();
  public static final CommandSpec LIST = listCommand();
  public static final CommandSpec VERSION = versionCommand();

  public static final ImmutableList<CommandSpec> COMMANDS = ImmutableList.of(
      BUILD, CLEAN, DAG, HELP, LIST, VERSION);

  public static int execute(String[] args) {
    if (args.length == 0) {
      return runCommand(Help.class, new String[] {"help"});
    }
    Optional<CommandSpec> spec = specForCommand(args[0]);
    if (spec.isEmpty()) {
      System.out.println("smooth: '" + args[0] + "' is not a smooth command. See 'smooth help'.");
      return EXIT_CODE_ERROR;
    } else {
      return runCommand(spec.get().commandClass(), args);
    }
  }

  public static Optional<CommandSpec> specForCommand(String commandName) {
    return COMMANDS.stream()
        .filter(c -> c.name().equals(commandName))
        .findFirst();
  }

  private static int runCommand(Class<? extends Command> command, String[] args) {
    String homeDir = System.getenv(SMOOTH_HOME_ENV_VARIABLE);
    if (homeDir == null) {
      System.out.println(
          "smooth: Environment variable '" + SMOOTH_HOME_ENV_VARIABLE + "' not set.");
      return EXIT_CODE_ERROR;
    }
    SmoothPaths smoothPaths = new SmoothPaths(Paths.get(homeDir));
    Injector injector = createInjector(new MainModule(smoothPaths));
    return injector.getInstance(command).run(args);
  }

  private static CommandSpec buildCommand() {
    return new CommandSpec(Build.class) {
      @Override
      public String description() {
        return unlines(
            "usage: smooth build <function>...",
            "",
            shortDescription(),
            "",
            "  <function>  function which execution result is saved as artifact"
        );
      }

      @Override
      public String shortDescription() {
        return "Build artifact(s) by running specified function(s)";
      }
    };
  }

  private static CommandSpec cleanCommand() {
    return new CommandSpec(Clean.class) {
      @Override
      public String description() {
        return unlines(
            "usage: smooth clean",
            "",
            shortDescription()
        );
      }

      @Override public String shortDescription() {
        return "Remove all cached objects and artifacts calculated during previous builds";
      }
    };
  }

  private static CommandSpec dagCommand() {
    return new CommandSpec(Dag.class) {
      @Override
      public String description() {
        return unlines(
            "usage: smooth dag <function>...",
            "",
            shortDescription()
        );
      }

      @Override
      public String shortDescription() {
        return "Prints execution DAG (directed acyclic graph) of for given function(s)";
      }
    };
  }

  private static CommandSpec helpCommand() {
    return new CommandSpec(Help.class) {
      @Override
      public String description() {
        return unlines(
            "usage: smooth help <command>",
            "",
            shortDescription(),
            "",
            "arguments:",
            "  <command>  command for which help is printed"
        );
      }

      @Override
      public String shortDescription() {
        return "Print help about given command";
      }
    };
  }

  private static CommandSpec listCommand() {
    return new CommandSpec(List.class) {
      @Override
      public String description() {
        return unlines(
            "usage: smooth list",
            "",
            shortDescription(),
            ""
        );
      }

      @Override
      public String shortDescription() {
        return "Print arg-less user defined functions";
      }
    };
  }

  private static CommandSpec versionCommand() {
    return new CommandSpec(Version.class) {
      @Override
      public String description() {
        return unlines(
            "usage: smooth version",
            "",
            shortDescription(),
            ""
        );
      }

      @Override
      public String shortDescription() {
        return "Print smooth build version number";
      }
    };
  }
}
