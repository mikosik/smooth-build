package org.smoothbuild.lang.builtin.java.javac.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.Message;

public class NoCompilerAvailableError extends Message {
  public NoCompilerAvailableError() {
    super(ERROR, "Couldn't find JavaCompiler implementation. "
        + "You have to run Smooth tool using JDK (not JVM). Only JDK contains java compiler.");
  }
}
