package org.smoothbuild.cli.handle;

import static org.smoothbuild.cli.spec.HelpSpec.ARGUMENT_NAME;

import javax.inject.Inject;

import net.sourceforge.argparse4j.inf.Namespace;

import org.smoothbuild.cli.CliParser;
import org.smoothbuild.cli.Command;

public class Help implements Handler {

  private final CliParser parser;

  @Inject
  public Help(CliParser parser) {
    this.parser = parser;
  }

  @Override
  public boolean run(Namespace namespace) {
    String commandName = (String) namespace.get(ARGUMENT_NAME);

    if (commandName == null) {
      parser.printHelp();
    } else {
      Command command = Command.forName(commandName);
      if (command == null) {
        System.out.println("Unknown command '" + commandName + "'");
      } else {
        parser.printHelpFor(command);
      }
    }
    return true;
  }
}
