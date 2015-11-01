package org.smoothbuild.builtin.java.javac.err;

import static org.smoothbuild.lang.message.MessageType.ERROR;

import org.smoothbuild.lang.message.Message;

public class NoCompilerAvailableError extends Message {
  public NoCompilerAvailableError() {
    super(ERROR, "Couldn't find JavaCompiler implementation. "
        + "You have to run Smooth tool using JDK (not JVM). Only JDK contains java compiler.");
  }
}
