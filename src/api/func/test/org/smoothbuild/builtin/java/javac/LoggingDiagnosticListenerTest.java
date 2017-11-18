package org.smoothbuild.builtin.java.javac;

import static org.hamcrest.Matchers.instanceOf;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.plugin.NativeApi;

public class LoggingDiagnosticListenerTest {
  private final Diagnostic<? extends JavaFileObject> diagnostic = mock(Diagnostic.class);
  private final NativeApi nativeApi = mock(NativeApi.class);
  private LoggingDiagnosticListener listener;

  @Before
  public void before() {
    given(willReturn(Diagnostic.Kind.ERROR), diagnostic).getKind();
    given(willReturn("diagnostic message"), diagnostic).getMessage(null);
  }

  @Test
  public void diagnostic_is_reported_as_error() {
    given(listener = new LoggingDiagnosticListener(nativeApi));
    when(listener).report(diagnostic);
    thenCalled(nativeApi).log(any(Message.class, instanceOf(ErrorMessage.class)));
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
