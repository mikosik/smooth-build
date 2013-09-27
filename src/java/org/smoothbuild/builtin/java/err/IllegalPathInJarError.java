package org.smoothbuild.builtin.java.err;

import org.smoothbuild.message.message.Error;

@SuppressWarnings("serial")
public class IllegalPathInJarError extends Error {
  public IllegalPathInJarError(String fileName) {
    super("File in a jar file has illegal name = '" + fileName + "'");
  }
}
