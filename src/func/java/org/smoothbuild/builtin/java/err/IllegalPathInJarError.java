package org.smoothbuild.builtin.java.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.Message;

public class IllegalPathInJarError extends Message {
  public IllegalPathInJarError(String fileName) {
    super(ERROR, "File in a jar file has illegal name = '" + fileName + "'");
  }
}
