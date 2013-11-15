package org.smoothbuild.lang.builtin.java.javac.err;

import static org.smoothbuild.message.base.MessageType.WARNING;

import org.smoothbuild.message.base.Message;

public class AdditionalCompilerInfo extends Message {
  public AdditionalCompilerInfo(String message) {
    super(WARNING, message);
  }
}
