package org.smoothbuild.message.listen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.testory.Testory.any;
import static org.testory.Testory.mock;
import static org.testory.Testory.onInstance;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledTimes;

import org.junit.Test;
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
  public void nothing_is_logged_on_successful_execution() throws Exception {
    MyNormalExecutor executor = new MyNormalExecutor(userConsole, name);
    executor.execute(value);

    thenCalledTimes(0, onInstance(userConsole));
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
  public void executor_logs_thrown_message_errors() throws Exception {
    MyThrowingExecutor executor = new MyThrowingExecutor(userConsole, name);
    executor.execute(value);

    thenCalled(userConsole).print(name, anyLoggedMessagesOf(value));
  }

  private static LoggedMessages anyLoggedMessagesOf(final String value) {
    return any(LoggedMessages.class, new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        LoggedMessages loggedMessages = (LoggedMessages) item;
        return Iterables.size(loggedMessages) == 1
            && Iterables.get(loggedMessages, 0).message().equals(value);
      }
    });
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
    MyLoggingAndThrowingFailedExceptionExecutor executor = new MyLoggingAndThrowingFailedExceptionExecutor(
        userConsole);
    executor.execute(value);

    thenCalled(userConsole).print(name, anyLoggedMessagesOf(value));
  }

  private static class MyLoggingAndThrowingFailedExceptionExecutor extends
      MessageCatchingExecutor<String, String> {
    private final LoggedMessages loggedMessages;

    public MyLoggingAndThrowingFailedExceptionExecutor(UserConsole userConsole) {
      this(userConsole, new LoggedMessages());
    }

    public MyLoggingAndThrowingFailedExceptionExecutor(UserConsole userConsole,
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
  public void additional_error_is_added_when_PhaseFailedException_is_thrown_without_logging_any_error()
      throws Exception {
    MyThrowingFailedExceptionExecutor executor = new MyThrowingFailedExceptionExecutor(userConsole);
    executor.execute(value);

    thenCalled(userConsole).print("name", any(LoggedMessages.class, new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        LoggedMessages loggedMessages = (LoggedMessages) item;
        return Iterables.size(loggedMessages) == 1
            && (Iterables.get(loggedMessages, 0) instanceof PhaseFailedWithoutErrorError);
      }
    }));
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
