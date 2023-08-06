package org.smoothbuild.stdlib.java.javac;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.evaluate.plugin.MessageLogger;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class LoggingDiagnosticListenerTest {
  private Diagnostic<? extends JavaFileObject> diagnostic;
  private NativeApi nativeApi;
  private MessageLogger messageLogger;

  @BeforeEach
  public void before() {
    diagnostic = mockDiagnostic();
    nativeApi = mock(NativeApi.class);
    messageLogger = mock(MessageLogger.class);
    when(diagnostic.getKind()).thenReturn(Kind.ERROR);
    when(diagnostic.getMessage(null)).thenReturn("diagnostic message");
    when(nativeApi.log()).thenReturn(messageLogger);
  }

  @SuppressWarnings("unchecked")
  private static Diagnostic<? extends JavaFileObject> mockDiagnostic() {
    return mock(Diagnostic.class);
  }

  @Test
  public void diagnostic_is_reported_as_error() throws Exception {
    LoggingDiagnosticListener listener = new LoggingDiagnosticListener(nativeApi);
    listener.report(diagnostic);
    verify(messageLogger).error(anyString());
  }

  @Test
  public void initially_no_error_is_reported() {
    LoggingDiagnosticListener listener = new LoggingDiagnosticListener(nativeApi);
    assertThat(listener.errorReported()).isFalse();
  }

  @Test
  public void error_reported_returns_true_when_diagnostic_has_been_reported() {
    LoggingDiagnosticListener listener = new LoggingDiagnosticListener(nativeApi);
    listener.report(diagnostic);
    assertThat(listener.errorReported()).isTrue();
  }
}
