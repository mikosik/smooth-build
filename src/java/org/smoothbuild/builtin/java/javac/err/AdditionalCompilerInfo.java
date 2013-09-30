package org.smoothbuild.builtin.java.javac.err;

import static org.smoothbuild.message.listen.MessageType.WARNING;

import org.smoothbuild.message.message.Message;

public class AdditionalCompilerInfo extends Message {
  public AdditionalCompilerInfo(String message) {
    super(WARNING, message);
  }
}
