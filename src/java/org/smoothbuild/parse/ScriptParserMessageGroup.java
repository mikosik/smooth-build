package org.smoothbuild.parse;

import javax.inject.Singleton;

import org.smoothbuild.message.listen.MessageGroup;

@Singleton
public class ScriptParserMessageGroup extends MessageGroup {
  private static final String SCRIPT_PARSING_PHASE_NAME = "parsing script";

  public ScriptParserMessageGroup() {
    super(SCRIPT_PARSING_PHASE_NAME);
  }
}
