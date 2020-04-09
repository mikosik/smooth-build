package org.smoothbuild.cli;

import java.io.PrintWriter;

import picocli.CommandLine;
import picocli.CommandLine.IParameterExceptionHandler;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.UnmatchedArgumentException;

public class CommandExecutor {
  public static int executeCommand(String[] args) {
    CommandLine commandLine = new CommandLine(new SmoothCommand())
        .setParameterExceptionHandler(new ShortErrorMessageHandler());
    return commandLine.execute(args);
  }

  private static class ShortErrorMessageHandler implements IParameterExceptionHandler {
    @Override
    public int handleParseException(ParameterException ex, String[] args) {
      CommandLine cmd = ex.getCommandLine();
      PrintWriter writer = cmd.getErr();

      writer.println(ex.getMessage());
      writer.println();
      UnmatchedArgumentException.printSuggestions(ex, writer);
      writer.print(cmd.getHelp().fullSynopsis());

      CommandSpec spec = cmd.getCommandSpec();
      String commandName = spec.name().equals("smooth") ? "" : " " + spec.name();
      writer.printf("Try 'smooth help%s' for more information.%n", commandName);

      return cmd.getExitCodeExceptionMapper() != null
          ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
          : spec.exitCodeOnInvalidInput();
    }
  }
}
