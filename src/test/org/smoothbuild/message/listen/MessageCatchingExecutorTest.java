package org.smoothbuild.message.listen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.smoothbuild.message.base.MessageType.ERROR;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.smoothbuild.message.base.Message;

import com.google.common.collect.Iterables;

public class MessageCatchingExecutorTest {
  private static final String name = "phase name";
  UserConsole userConsole = mock(UserConsole.class);
  String value = "value";

  @Test
  public void executor_returns_value() throws Exception {
    MyNormalExecutor executor = new MyNormalExecutor(userConsole, name);
    String result = executor.execute(value);
    assertThat(result).isEqualTo(value);
  }

  @Test
  public void nothing_is_reported_on_successful_execution() throws Exception {
    MyNormalExecutor executor = new MyNormalExecutor(userConsole, name);
    executor.execute(value);

    verifyZeroInteractions(userConsole);
  }

  private static class MyNormalExecutor extends MessageCatchingExecutor<String, String> {
    public MyNormalExecutor(UserConsole userConsole, String name) {
      super(userConsole, name, new LoggedMessages());
    }

    @Override
    public String executeImpl(String arguments) {
      return arguments;
    }
  }

  @Test
  public void executor_reports_thrown_message_errors() throws Exception {
    MyThrowingExecutor executor = new MyThrowingExecutor(userConsole, name);
    executor.execute(value);

    ArgumentCaptor<LoggedMessages> captured = ArgumentCaptor.forClass(LoggedMessages.class);
    verify(userConsole).report(eq(name), captured.capture());
    assertThat(Iterables.size(captured.getValue())).isEqualTo(1);
    assertThat(Iterables.get(captured.getValue(), 0).message()).isEqualTo(value);
  }

  private static class MyThrowingExecutor extends MessageCatchingExecutor<String, String> {
    public MyThrowingExecutor(UserConsole userConsole, String name) {
      super(userConsole, name, new LoggedMessages());
    }

    @Override
    public String executeImpl(String arguments) {
      throw new ErrorMessageException(new Message(ERROR, arguments));
    }
  }

  @Test
  public void phase_failed_exception_is_caught() throws Exception {
    MyReportingAndThrowingFailedExceptionExecutor executor =
        new MyReportingAndThrowingFailedExceptionExecutor(userConsole);
    executor.execute(value);

    ArgumentCaptor<LoggedMessages> captured = ArgumentCaptor.forClass(LoggedMessages.class);
    verify(userConsole).report(eq(name), captured.capture());
    assertThat(Iterables.size(captured.getValue())).isEqualTo(1);
    assertThat(Iterables.get(captured.getValue(), 0).message()).isEqualTo(value);
  }

  private static class MyReportingAndThrowingFailedExceptionExecutor extends
      MessageCatchingExecutor<String, String> {
    private final LoggedMessages loggedMessages;

    public MyReportingAndThrowingFailedExceptionExecutor(UserConsole userConsole) {
      this(userConsole, new LoggedMessages());
    }

    public MyReportingAndThrowingFailedExceptionExecutor(UserConsole userConsole,
        LoggedMessages loggedMessages) {
      super(userConsole, name, loggedMessages);
      this.loggedMessages = loggedMessages;
    }

    @Override
    public String executeImpl(String arguments) {
      loggedMessages.log(new Message(ERROR, arguments));
      throw new PhaseFailedException();
    }
  }

  @Test
  public void additional_error_is_added_when_PhaseFailedException_is_thrown_without_reporting_any_error()
      throws Exception {
    MyThrowingFailedExceptionExecutor executor = new MyThrowingFailedExceptionExecutor(userConsole);
    executor.execute(value);

    ArgumentCaptor<LoggedMessages> captured = ArgumentCaptor.forClass(LoggedMessages.class);
    verify(userConsole).report(eq("name"), captured.capture());
    assertThat(Iterables.size(captured.getValue())).isEqualTo(1);
    assertThat(Iterables.get(captured.getValue(), 0)).isInstanceOf(
        PhaseFailedWithoutErrorError.class);
  }

  private static class MyThrowingFailedExceptionExecutor extends
      MessageCatchingExecutor<String, String> {

    public MyThrowingFailedExceptionExecutor(UserConsole userConsole) {
      super(userConsole, "name", new LoggedMessages());
    }

    @Override
    public String executeImpl(String arguments) {
      throw new PhaseFailedException();
    }
  }
}
