package org.smoothbuild.builtin.java.junit.err;

import static org.smoothbuild.lang.message.MessageType.WARNING;

import org.smoothbuild.lang.message.Message;

public class NoJunitTestFoundWarning extends Message {
  public NoJunitTestFoundWarning() {
    super(WARNING, "No junit tests found.");
  }
}
