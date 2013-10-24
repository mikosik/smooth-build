package org.smoothbuild.builtin.java.javac;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import org.smoothbuild.builtin.java.javac.err.JavaCompilerMessage;
import org.smoothbuild.plugin.Sandbox;

public class ReportingDiagnosticListener implements DiagnosticListener<JavaFileObject> {
  private final Sandbox sandbox;
  private boolean errorReported;

  public ReportingDiagnosticListener(Sandbox sandbox) {
    this.sandbox = sandbox;
    this.errorReported = false;
  }

  @Override
  public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
    sandbox.report(new JavaCompilerMessage(diagnostic));
    errorReported = true;
  }

  public boolean errorReported() {
    return errorReported;
  }
}
