package org.smoothbuild.builtin.java.javac;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import org.smoothbuild.builtin.java.javac.err.JavaCompilerError;
import org.smoothbuild.problem.ProblemsListener;

public class ReportingDiagnosticListener implements DiagnosticListener<JavaFileObject> {
  private final ProblemsListener problems;
  private boolean errorReported;

  public ReportingDiagnosticListener(ProblemsListener problems) {
    this.problems = problems;
    this.errorReported = false;
  }

  @Override
  public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
    problems.report(new JavaCompilerError(diagnostic.toString()));
    errorReported = true;
  }

  public boolean errorReported() {
    return errorReported;
  }
}
