package org.smoothbuild.builtin.java.err;

import org.smoothbuild.message.message.ErrorMessage;

public class IllegalPathInJarError extends ErrorMessage {
  public IllegalPathInJarError(String fileName) {
    super("File in a jar file has illegal name = '" + fileName + "'");
  }
}
