package org.smoothbuild.cli;

import static com.google.inject.Guice.createInjector;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.SMOOTH_HOME_ENV_VARIABLE;

import java.nio.file.Paths;

import org.smoothbuild.MainModule;
import org.smoothbuild.SmoothPaths;

import com.google.inject.Injector;

public class Commands {
  public static final String BUILD = "build";
  public static final String CLEAN = "clean";
  public static final String DAG = "dag";
  public static final String HELP = "help";
  public static final String LIST = "list";
  public static final String VERSION = "version";

  public static int execute(String[] args) {
    if (args.length == 0) {
      args = new String[] { HELP };
    }
    switch (args[0]) {
      case BUILD:
        return runCommand(Build.class, args);
      case CLEAN:
        return runCommand(Clean.class, args);
      case DAG:
        return runCommand(Dag.class, args);
      case HELP:
        return runCommand(Help.class, args);
      case LIST:
        return runCommand(List.class, args);
      case VERSION:
        return runCommand(Version.class, args);
      default:
        System.out.println("smooth: '" + args[0]
            + "' is not a smooth command. See 'smooth help'.");
        return EXIT_CODE_ERROR;
    }
  }

  private static int runCommand(Class<? extends Command> command, String[] args) {
    String homeDir = System.getenv(SMOOTH_HOME_ENV_VARIABLE);
    if (homeDir == null) {
      System.out.println("smooth: Environment variable '" + SMOOTH_HOME_ENV_VARIABLE
          + "' not set.");
      return EXIT_CODE_ERROR;
    }
    SmoothPaths smoothPaths = new SmoothPaths(Paths.get(homeDir));
    Injector injector = createInjector(new MainModule(smoothPaths));
    return injector.getInstance(command).run(args);
  }
}
