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
      return create(Help.class).run(new String[] { HELP });
    }
    switch (args[0]) {
      case BUILD:
        return create(Build.class).run(args);
      case CLEAN:
        return create(Clean.class).run(args);
      case HELP:
        return create(Help.class).run(args);
      default:
        System.out.println("smooth: '" + args[0]
            + "' is not a smooth command. See 'smooth help'.");
        return EXIT_CODE_ERROR;
    }
  }

  private static <T> T create(Class<? extends T> clazz) {
    return createInjector(new MainModule()).getInstance(clazz);
  }
}
