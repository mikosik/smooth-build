package org.smoothbuild.stdlib.java.javac;

import static org.smoothbuild.common.Throwables.unexpectedCaseExc;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class LoggingDiagnosticListener implements DiagnosticListener<JavaFileObject> {
  private final NativeApi nativeApi;
  private boolean errorReported;

  public LoggingDiagnosticListener(NativeApi nativeApi) {
    this.nativeApi = nativeApi;
    this.errorReported = false;
  }

  @Override
  public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
    switch (diagnostic.getKind()) {
      case ERROR -> nativeApi.log().error(diagnostic.getMessage(null));
      case MANDATORY_WARNING, WARNING -> nativeApi.log().warning(diagnostic.getMessage(null));
      case NOTE, OTHER -> nativeApi.log().info(diagnostic.getMessage(null));
      default -> throw unexpectedCaseExc(diagnostic.getKind());
    }
    errorReported = true;
  }

  public boolean errorReported() {
    return errorReported;
  }
}
