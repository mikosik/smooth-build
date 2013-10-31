package org.smoothbuild.parse;

import javax.inject.Singleton;

import org.smoothbuild.message.listen.MessageGroup;

@Singleton
public class ModuleParserMessages extends MessageGroup {
  public ModuleParserMessages() {
    super("SCRIPT PARSER");
  }
}
