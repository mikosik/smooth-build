package org.smoothbuild.builtin.java.javac.err;

import org.smoothbuild.message.message.Error;

@SuppressWarnings("serial")
public class NoCompilerAvailableError extends Error {
  public NoCompilerAvailableError() {
    super("Couldn't find JavaCompiler implementation. "
        + "You have to run Smooth tool using JDK (not JVM). Only JDK contains java compiler.");
  }
}
