package org.smoothbuild;

import io.airlift.command.Cli;
import io.airlift.command.Cli.CliBuilder;

import org.smoothbuild.app.BuildCommand;
import org.smoothbuild.app.CleanCommand;
import org.smoothbuild.app.HelpCommand;
import org.smoothbuild.app.RunnableCommand;

public class Main {
  public static void main(String[] args) {
    CliBuilder<RunnableCommand> builder = Cli.<RunnableCommand> builder("smooth");
    builder.withDescription("powerful build tool with simple language");
    builder.withDefaultCommand(HelpCommand.class);
    builder.withCommand(HelpCommand.class);
    builder.withCommand(BuildCommand.class);
    builder.withCommand(CleanCommand.class);
    Cli<RunnableCommand> cliParser = builder.build();

    boolean success = cliParser.parse(args).runCommand();
    System.exit(success ? 0 : 1);
  }
}
