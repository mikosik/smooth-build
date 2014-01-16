package org.smoothbuild.lang.builtin.java.javac;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import org.smoothbuild.lang.builtin.java.javac.err.JavaCompilerMessage;
import org.smoothbuild.lang.plugin.PluginApi;

public class ReportingDiagnosticListener implements DiagnosticListener<JavaFileObject> {
  private final PluginApi pluginApi;
  private boolean errorReported;

  public ReportingDiagnosticListener(PluginApi pluginApi) {
    this.pluginApi = pluginApi;
    this.errorReported = false;
  }

  @Override
  public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
    pluginApi.log(new JavaCompilerMessage(diagnostic));
    errorReported = true;
  }

  public boolean errorReported() {
    return errorReported;
  }
}
