package org.smoothbuild.lang.builtin.java.javac;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.smoothbuild.lang.builtin.java.javac.err.JavaCompilerMessage;
import org.smoothbuild.lang.plugin.PluginApi;
import org.smoothbuild.message.base.Message;

public class LoggingDiagnosticListenerTest {
  @SuppressWarnings("unchecked")
  Diagnostic<? extends JavaFileObject> diagnostic = mock(Diagnostic.class);
  PluginApi pluginApi = mock(PluginApi.class);
  LoggingDiagnosticListener listener = new LoggingDiagnosticListener(pluginApi);

  @Before
  public void before() {
    given(willReturn(Diagnostic.Kind.ERROR), diagnostic).getKind();
    given(willReturn("diagnostic message"), diagnostic).getMessage(null);
  }

  @Test
  public void diagnosticIsReportedAsError() {
    when(listener).report(diagnostic);
    thenCalled(pluginApi).log(any(Message.class, instanceOf(JavaCompilerMessage.class)));
  }

  @Test
  public void initiallyNoErrorsReported() throws Exception {
    assertThat(listener.errorReported()).isFalse();
  }

  @Test
  public void diagnosticMakesErrorReportedReturnsTrue() throws Exception {
    given(listener).report(diagnostic);
    when(listener).errorReported();
    thenReturned(true);
  }
}
