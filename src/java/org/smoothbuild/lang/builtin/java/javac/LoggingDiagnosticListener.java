package org.smoothbuild.lang.builtin.java.javac;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.builtin.java.javac.err.JavaCompilerMessage;

public class LoggingDiagnosticListener implements DiagnosticListener<JavaFileObject> {
  private final NativeApi nativeApi;
  private boolean errorReported;

  public LoggingDiagnosticListener(NativeApi nativeApi) {
    this.nativeApi = nativeApi;
    this.errorReported = false;
  }

  @Override
  public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
    nativeApi.log(new JavaCompilerMessage(diagnostic));
    errorReported = true;
  }

  public boolean errorReported() {
    return errorReported;
  }
}
