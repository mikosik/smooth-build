package org.smoothbuild.cli;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class Commands {
  public static final Map<String, Command> COMMANDS = commands();

  private static Map<String, Command> commands() {
    Builder<String, Command> builder = ImmutableMap.builder();
    builder.put("build", new BuildCommand());
    builder.put("clean", new CleanCommand());
    builder.put("help", new HelpCommand());
    return builder.build();
  }
}
