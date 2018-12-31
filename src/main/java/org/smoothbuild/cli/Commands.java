package org.smoothbuild.cli;

import static com.google.inject.Guice.createInjector;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.SMOOTH_HOME_ENV_VARIABLE;

import java.nio.file.Paths;

import org.smoothbuild.MainModule;
import org.smoothbuild.SmoothPaths;

import com.google.common.collect.ImmutableMap;
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
      args = new String[] {HELP};
    }
    Class<? extends Command> commandClass = commands().get(args[0]);
    if (commandClass == null) {
      System.out.println("smooth: '" + args[0]
          + "' is not a smooth command. See 'smooth help'.");
      return EXIT_CODE_ERROR;
    } else {
      return runCommand(commandClass, args);
    }
  }

  private static ImmutableMap<String, Class<? extends Command>> commands() {
    return ImmutableMap.<String, Class<? extends Command>>builder()
        .put(BUILD, Build.class)
        .put(CLEAN, Clean.class)
        .put(DAG, Dag.class)
        .put(HELP, Help.class)
        .put(LIST, List.class)
        .put(VERSION, Version.class)
        .build();
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
