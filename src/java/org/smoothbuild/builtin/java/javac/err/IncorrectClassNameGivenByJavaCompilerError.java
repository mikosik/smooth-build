package org.smoothbuild.builtin.java.javac.err;

import org.smoothbuild.message.Error;

public class IncorrectClassNameGivenByJavaCompilerError extends Error {
  public IncorrectClassNameGivenByJavaCompilerError(String className) {
    super("Internal Error: JavaCompiler passed illegal class name = '" + className
        + "' to JavaFileManager.");
  }
}
