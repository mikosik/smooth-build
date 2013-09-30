package org.smoothbuild.builtin.java.javac.err;

import org.smoothbuild.message.message.ErrorMessage;

public class IncorrectClassNameGivenByJavaCompilerError extends ErrorMessage {
  public IncorrectClassNameGivenByJavaCompilerError(String className) {
    super("Internal Error: JavaCompiler passed illegal class name = '" + className
        + "' to JavaFileManager.");
  }
}
