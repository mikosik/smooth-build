package org.smoothbuild;

import io.airlift.command.Cli;
import io.airlift.command.Cli.CliBuilder;
import io.airlift.command.Help;

import org.smoothbuild.app.BuildCommand;
import org.smoothbuild.app.CleanCommand;

public class Main {
  public static void main(String[] args) {
    CliBuilder<Runnable> builder = Cli.<Runnable> builder("smooth");
    builder.withDescription("powerful build tool with simple language");
    builder.withDefaultCommand(Help.class);
    builder.withCommand(Help.class);
    builder.withCommand(BuildCommand.class);
    builder.withCommand(CleanCommand.class);
    Cli<Runnable> cliParser = builder.build();

    cliParser.parse(args).run();
  }
}
