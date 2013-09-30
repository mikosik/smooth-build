package org.smoothbuild.builtin.java.javac.err;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.MessageType;

public class JavaCompilerMessage extends Message {
  public JavaCompilerMessage(Diagnostic<? extends JavaFileObject> diagnostic) {
    super(typeOf(diagnostic), diagnostic.getMessage(null));
  }

  private static MessageType typeOf(Diagnostic<? extends JavaFileObject> diagnostic) {
    switch (diagnostic.getKind()) {
      case ERROR:
        return MessageType.ERROR;
      case MANDATORY_WARNING:
        return MessageType.WARNING;
      case WARNING:
        return MessageType.WARNING;
      case NOTE:
        return MessageType.INFO;
      case OTHER:
        return MessageType.INFO;
      default:
        throw new RuntimeException("Unknown diagnostic kind " + diagnostic.getKind());
    }
  }
}
