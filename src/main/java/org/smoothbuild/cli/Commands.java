package org.smoothbuild.cli;

import static com.google.inject.Guice.createInjector;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;

import java.util.Map;

import org.smoothbuild.MainModule;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class Commands {
  public static final Map<String, CommandSpec> COMMANDS = commandSpecs();

  private static Map<String, CommandSpec> commandSpecs() {
    Builder<String, CommandSpec> builder = ImmutableMap.builder();
    builder.put("build", new BuildSpec());
    builder.put("clean", new CleanSpec());
    builder.put("help", new HelpSpec());
    return builder.build();
  }

  public static int execute(String[] args) {
    if (args.length == 0) {
      args = new String[] { "help" };
    }
    String commandName = args[0];
    CommandSpec commandSpec = COMMANDS.get(commandName);
    if (commandSpec == null) {
      System.out.println("smooth: '" + commandName
          + "' is not a smooth command. See 'smooth help'.");
      return EXIT_CODE_ERROR;
    } else {
      return createInjector(new MainModule()).getInstance(commandSpec.commandClass()).run(args);
    }
  }
}
