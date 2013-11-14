package org.smoothbuild.command;

import javax.inject.Singleton;

import org.smoothbuild.message.listen.MessageGroup;

@Singleton
public class CommandLineParserMessages extends MessageGroup {
  public CommandLineParserMessages() {
    super("COMMAND LINE PARSER");
  }
}
