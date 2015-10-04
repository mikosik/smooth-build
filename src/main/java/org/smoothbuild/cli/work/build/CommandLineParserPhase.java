package org.smoothbuild.cli.work.build;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.listen.MessageCatchingExecutor;
import org.smoothbuild.message.listen.UserConsole;

public class CommandLineParserPhase extends MessageCatchingExecutor<List<String>, Set<Name>> {

  private final CommandLineParser commandLineParser;

  @Inject
  public CommandLineParserPhase(UserConsole userConsole,
      CommandLineParserMessages commandLineParserMessages, CommandLineParser commandLineParser) {
    super(userConsole, "COMMAND LINE PARSER", commandLineParserMessages);
    this.commandLineParser = commandLineParser;
  }

  @Override
  public Set<Name> executeImpl(List<String> arguments) {
    return commandLineParser.parse(arguments);
  }
}
