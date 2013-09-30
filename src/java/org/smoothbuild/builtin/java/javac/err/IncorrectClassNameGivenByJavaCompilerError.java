package org.smoothbuild.builtin.java.javac.err;

import static org.smoothbuild.message.listen.MessageType.ERROR;

import org.smoothbuild.message.message.Message;

public class IncorrectClassNameGivenByJavaCompilerError extends Message {
  public IncorrectClassNameGivenByJavaCompilerError(String className) {
    super(ERROR, "Internal Error: JavaCompiler passed illegal class name = '" + className
        + "' to JavaFileManager.");
  }
}
