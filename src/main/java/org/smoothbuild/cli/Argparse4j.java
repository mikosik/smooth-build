package org.smoothbuild.cli;

import static org.smoothbuild.cli.CliParser.COMMAND_IMPLEMENTATION_DEST;

import javax.inject.Inject;

import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import org.smoothbuild.cli.handle.Handler;

import com.google.inject.Injector;

public class Argparse4j {
  private final Injector injector;
  private final CliParser parser;

  @Inject
  public Argparse4j(Injector injector, CliParser parser) {
    this.injector = injector;
    this.parser = parser;
  }

  public int parse(String[] args) {
    args = convertArgs(args);

    ArgumentParser argumentParser = parser.parser();
    try {
      Namespace namespace = argumentParser.parseArgs(args);
      Class<? extends Handler> handler = namespace.get(COMMAND_IMPLEMENTATION_DEST);
      return injector.getInstance(handler).run(namespace);
    } catch (ArgumentParserException e) {
      argumentParser.handleError(e);
      return 1;
    }
  }

  private static String[] convertArgs(String[] args) {
    int argsCount = args.length;
    switch (argsCount) {
      case 0:
        return singleHelpCommandArgument();
      case 1:
        String onlyArg = args[0];
        if ("--help".equals(onlyArg) || "-h".equals(onlyArg)) {
          return singleHelpCommandArgument();
        }
        return args;
      default:
        return args;
    }
  }

  private static String[] singleHelpCommandArgument() {
    return new String[] { "help" };
  }
}
