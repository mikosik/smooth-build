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
import org.smoothbuild.builtin.java.javac.err.JavaCompilerMessage;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.message.base.Message;

public class LoggingDiagnosticListenerTest {
  private final Diagnostic<? extends JavaFileObject> diagnostic = mock(Diagnostic.class);
  private final Container container = mock(Container.class);
  private LoggingDiagnosticListener listener;

  @Before
  public void before() {
    given(willReturn(Diagnostic.Kind.ERROR), diagnostic).getKind();
    given(willReturn("diagnostic message"), diagnostic).getMessage(null);
  }

  @Test
  public void diagnosticIsReportedAsError() {
    given(listener = new LoggingDiagnosticListener(container));
    when(listener).report(diagnostic);
    thenCalled(container).log(any(Message.class, instanceOf(JavaCompilerMessage.class)));
  }

  @Test
  public void initiallyNoErrorsReported() throws Exception {
    given(listener = new LoggingDiagnosticListener(container));
    when(listener).errorReported();
    thenReturned(false);
  }

  @Test
  public void diagnosticMakesErrorReportedReturnsTrue() throws Exception {
    given(listener = new LoggingDiagnosticListener(container));
    given(listener).report(diagnostic);
    when(listener).errorReported();
    thenReturned(true);
  }
}
