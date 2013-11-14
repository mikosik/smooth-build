package org.smoothbuild.command;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.message.listen.MessageCatchingExecutor;
import org.smoothbuild.message.listen.UserConsole;

public class CommandLineParserPhase extends
    MessageCatchingExecutor<List<String>, CommandLineArguments> {

  private final CommandLineParser commandLineParser;

  @Inject
  public CommandLineParserPhase(UserConsole userConsole,
      CommandLineParserMessages commandLineParserMessages, CommandLineParser commandLineParser) {
    super(userConsole, commandLineParserMessages);
    this.commandLineParser = commandLineParser;
  }

  @Override
  public CommandLineArguments executeImpl(List<String> arguments) {
    return commandLineParser.parse(arguments);
  }
}
