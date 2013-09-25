package org.smoothbuild.builtin.java.javac.err;

import org.smoothbuild.message.Error;

@SuppressWarnings("serial")
public class JavaCompilerError extends Error {
  public JavaCompilerError(String message) {
    super(message);
  }
}
