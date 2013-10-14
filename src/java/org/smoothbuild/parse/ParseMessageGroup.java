package org.smoothbuild.parse;

import javax.inject.Singleton;

import org.smoothbuild.message.listen.MessageGroup;

@Singleton
public class ParseMessageGroup extends MessageGroup {
  public ParseMessageGroup() {
    super("parsing script");
  }
}
