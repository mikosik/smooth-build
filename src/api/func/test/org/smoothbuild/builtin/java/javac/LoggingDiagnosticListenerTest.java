package org.smoothbuild.builtin.java.javac;

import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.plugin.MessageLogger;
import org.smoothbuild.lang.plugin.NativeApi;

public class LoggingDiagnosticListenerTest {
  private final Diagnostic<? extends JavaFileObject> diagnostic = mock(Diagnostic.class);
  private final NativeApi nativeApi = mock(NativeApi.class);
  private final MessageLogger messageLogger = mock(MessageLogger.class);
  private LoggingDiagnosticListener listener;

  @BeforeEach
  public void before() {
    given(willReturn(Diagnostic.Kind.ERROR), diagnostic).getKind();
    given(willReturn("diagnostic message"), diagnostic).getMessage(null);
    given(willReturn(messageLogger), nativeApi).log();
  }

  @Test
  public void diagnostic_is_reported_as_error() {
    given(listener = new LoggingDiagnosticListener(nativeApi));
    when(listener).report(diagnostic);
    thenCalled(messageLogger).error(any(String.class));
  }

  @Test
  public void initially_no_error_is_reported() throws Exception {
    given(listener = new LoggingDiagnosticListener(nativeApi));
    when(listener).errorReported();
    thenReturned(false);
  }

  @Test
  public void error_reported_returns_true_when_diagnostic_has_been_reported() throws Exception {
    given(listener = new LoggingDiagnosticListener(nativeApi));
    given(listener).report(diagnostic);
    when(listener).errorReported();
    thenReturned(true);
  }
}
