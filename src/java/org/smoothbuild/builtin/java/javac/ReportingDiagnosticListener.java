package org.smoothbuild.builtin.java.javac;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import org.smoothbuild.builtin.java.javac.err.JavaCompilerError;
import org.smoothbuild.problem.MessageListener;

public class ReportingDiagnosticListener implements DiagnosticListener<JavaFileObject> {
  private final MessageListener messages;
  private boolean errorReported;

  public ReportingDiagnosticListener(MessageListener messages) {
    this.messages = messages;
    this.errorReported = false;
  }

  @Override
  public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
    messages.report(new JavaCompilerError(diagnostic.toString()));
    errorReported = true;
  }

  public boolean errorReported() {
    return errorReported;
  }
}
