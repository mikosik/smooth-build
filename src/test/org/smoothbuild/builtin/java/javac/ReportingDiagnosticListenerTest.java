package org.smoothbuild.builtin.java.javac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.builtin.java.javac.err.JavaCompilerMessage;
import org.smoothbuild.plugin.api.Sandbox;

public class ReportingDiagnosticListenerTest {
  @SuppressWarnings("unchecked")
  Diagnostic<? extends JavaFileObject> diagnostic = mock(Diagnostic.class);
  Sandbox sandbox = mock(Sandbox.class);
  ReportingDiagnosticListener listener = new ReportingDiagnosticListener(sandbox);

  @Before
  public void before() {
    when(diagnostic.getKind()).thenReturn(Diagnostic.Kind.ERROR);
    when(diagnostic.getMessage(null)).thenReturn("diagnostic message");
  }

  @Test
  public void diagnosticIsReportedAsError() {
    listener.report(diagnostic);
    verify(sandbox).report(isA(JavaCompilerMessage.class));
  }

  @Test
  public void initiallyNoErrorsReported() throws Exception {
    assertThat(listener.errorReported()).isFalse();
  }

  @Test
  public void diagnosticMakesErrorReportedReturnsTrue() throws Exception {
    listener.report(diagnostic);
    assertThat(listener.errorReported()).isTrue();
  }
}
