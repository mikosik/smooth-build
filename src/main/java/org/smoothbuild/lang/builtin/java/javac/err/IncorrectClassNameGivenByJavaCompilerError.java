package org.smoothbuild.lang.builtin.java.javac.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.Message;

@SuppressWarnings("serial")
public class IncorrectClassNameGivenByJavaCompilerError extends Message {
  public IncorrectClassNameGivenByJavaCompilerError(String className) {
    super(ERROR, "Internal Error: JavaCompiler passed illegal class name = '" + className
        + "' to JavaFileManager.");
  }
}
