package org.smoothbuild.builtin.java.javac;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.message.InfoMessage;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.message.WarningMessage;
import org.smoothbuild.lang.plugin.Container;

public class LoggingDiagnosticListener implements DiagnosticListener<JavaFileObject> {
  private final Container container;
  private boolean errorReported;

  public LoggingDiagnosticListener(Container container) {
    this.container = container;
    this.errorReported = false;
  }

  @Override
  public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
    container.log(newMessage(diagnostic));
    errorReported = true;
  }

  private static Message newMessage(Diagnostic<? extends JavaFileObject> diagnostic) {
    switch (diagnostic.getKind()) {
      case ERROR:
        return new ErrorMessage(diagnostic.getMessage(null));
      case MANDATORY_WARNING:
      case WARNING:
        return new WarningMessage(diagnostic.getMessage(null));
      case NOTE:
      case OTHER:
        return new InfoMessage(diagnostic.getMessage(null));
      default:
        throw new RuntimeException("Unknown diagnostic kind " + diagnostic.getKind());
    }
  }

  public boolean errorReported() {
    return errorReported;
  }
}
