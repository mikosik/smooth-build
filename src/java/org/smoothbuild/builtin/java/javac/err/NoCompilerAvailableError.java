package org.smoothbuild.builtin.java.javac.err;

import org.smoothbuild.message.message.ErrorMessage;

public class NoCompilerAvailableError extends ErrorMessage {
  public NoCompilerAvailableError() {
    super("Couldn't find JavaCompiler implementation. "
        + "You have to run Smooth tool using JDK (not JVM). Only JDK contains java compiler.");
  }
}
