package org.smoothbuild.cli;

import static com.google.inject.Guice.createInjector;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;

import org.smoothbuild.MainModule;

public class Commands {
  public static final String BUILD = "build";
  public static final String CLEAN = "clean";
  public static final String HELP = "help";

  public static int execute(String[] args) {
    if (args.length == 0) {
      return runCommand(Help.class, new String[] { HELP });
    }
    switch (args[0]) {
      case BUILD:
        return runCommand(Build.class, args);
      case CLEAN:
        return runCommand(Clean.class, args);
      case HELP:
        return runCommand(Help.class, args);
      default:
        System.out.println("smooth: '" + args[0]
            + "' is not a smooth command. See 'smooth help'.");
        return EXIT_CODE_ERROR;
    }
  }

  private static int runCommand(Class<? extends Command> commandClass, String[] args) {
    return createInjector(new MainModule()).getInstance(commandClass).run(args);
  }
}
