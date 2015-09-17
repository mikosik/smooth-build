package org.smoothbuild.builtin.java.javac;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import org.smoothbuild.builtin.java.javac.err.JavaCompilerMessage;
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
    container.log(new JavaCompilerMessage(diagnostic));
    errorReported = true;
  }

  public boolean errorReported() {
    return errorReported;
  }
}
