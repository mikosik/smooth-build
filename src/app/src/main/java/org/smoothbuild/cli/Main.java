package org.smoothbuild.cli;

import static org.smoothbuild.common.Constants.CHARSET;

import java.io.PrintStream;
import java.io.PrintWriter;
import org.smoothbuild.cli.command.SmoothCommand;
import picocli.CommandLine;
import picocli.CommandLine.IParameterExceptionHandler;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.UnmatchedArgumentException;

public class Main {
  public static void main(String[] args) {
    PrintWriter out = printWriter(System.out);
    PrintWriter err = printWriter(System.err);
    int exitCode = runSmooth(args, out, err);
    System.exit(exitCode);
  }

  public static int runSmooth(String[] args, PrintWriter out, PrintWriter err) {
    CommandLine commandLine = new CommandLine(new SmoothCommand())
        .setOut(out)
        .setErr(err)
        .setParameterExceptionHandler(new ShortErrorMessageHandler());
    return commandLine.execute(args);
  }

  private static PrintWriter printWriter(PrintStream printStream) {
    return new PrintWriter(printStream, true, CHARSET);
  }

  private static class ShortErrorMessageHandler implements IParameterExceptionHandler {
    @Override
    public int handleParseException(ParameterException parameterException, String[] args) {
      CommandLine cmd = parameterException.getCommandLine();
      PrintWriter writer = cmd.getErr();

      writer.println(parameterException.getMessage());
      writer.println();
      UnmatchedArgumentException.printSuggestions(parameterException, writer);
      writer.print(cmd.getHelp().fullSynopsis());

      CommandSpec spec = cmd.getCommandSpec();
      String commandName = spec.name().equals("smooth") ? "" : " " + spec.name();
      writer.printf("Try 'smooth help%s' for more information.%n", commandName);

      return cmd.getExitCodeExceptionMapper() != null
          ? cmd.getExitCodeExceptionMapper().getExitCode(parameterException)
          : spec.exitCodeOnInvalidInput();
    }
  }
}
