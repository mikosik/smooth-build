package org.smoothbuild.command;

import javax.inject.Inject;

import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.message.message.ErrorMessageException;

public class CommandLineParserExecutor {
  private final UserConsole userConsole;
  private final CommandLineParser commandLineParser;

  @Inject
  public CommandLineParserExecutor(UserConsole userConsole, CommandLineParser commandLineParser) {
    this.userConsole = userConsole;
    this.commandLineParser = commandLineParser;
  }

  public CommandLineArguments parse(String... args) {
    MessageGroup messageGroup = new MessageGroup("parsing arguments");
    try {
      return commandLineParser.parse(args);
    } catch (ErrorMessageException e) {
      messageGroup.report(e.errorMessage());
    } finally {
      userConsole.report(messageGroup);
    }
    return null;
  }
}
