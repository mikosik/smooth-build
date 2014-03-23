package org.smoothbuild.lang.builtin.java.junit.err;

import static org.smoothbuild.message.base.MessageType.WARNING;

import org.smoothbuild.message.base.Message;

@SuppressWarnings("serial")
public class NoJunitTestFoundWarning extends Message {
  public NoJunitTestFoundWarning() {
    super(WARNING, "No junit tests found.");
  }
}
