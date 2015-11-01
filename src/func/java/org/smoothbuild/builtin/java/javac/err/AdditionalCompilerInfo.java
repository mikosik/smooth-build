package org.smoothbuild.builtin.java.javac.err;

import static org.smoothbuild.lang.message.MessageType.WARNING;

import org.smoothbuild.lang.message.Message;

public class AdditionalCompilerInfo extends Message {
  public AdditionalCompilerInfo(String message) {
    super(WARNING, message);
  }
}
