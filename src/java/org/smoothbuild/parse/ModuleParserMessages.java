package org.smoothbuild.parse;

import javax.inject.Singleton;

import org.smoothbuild.message.listen.MessageGroup;

@Singleton
public class ModuleParserMessages extends MessageGroup {
  private static final String MODULE_PARSER_PHASE_NAME = "parsing script";

  public ModuleParserMessages() {
    super(MODULE_PARSER_PHASE_NAME);
  }
}
