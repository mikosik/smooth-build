package org.smoothbuild.builtin.java.err;

import static org.smoothbuild.message.listen.MessageType.ERROR;

import org.smoothbuild.message.message.Message;

public class IllegalPathInJarError extends Message {
  public IllegalPathInJarError(String fileName) {
    super(ERROR, "File in a jar file has illegal name = '" + fileName + "'");
  }
}
