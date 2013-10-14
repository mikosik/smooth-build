package org.smoothbuild.message.listen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.message.message.MessageType.ERROR;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.smoothbuild.message.message.ErrorMessageException;
import org.smoothbuild.message.message.Message;

import com.google.common.collect.Iterables;

public class MessageCatchingExecutorTest {
  UserConsole userConsole = mock(UserConsole.class);
  String name = "phase name";
  String value = "value";

  @Test
  public void executor_returns_value() throws Exception {
    MyNormalExecutor executor = new MyNormalExecutor(userConsole, name);
    String result = executor.execute(value);
    assertThat(result).isEqualTo(value);
  }

  @Test
  public void message_group_with_no_messages_is_reported_on_successful_execution() throws Exception {
    MyNormalExecutor executor = new MyNormalExecutor(userConsole, name);
    executor.execute(value);

    ArgumentCaptor<MessageGroup> captured = ArgumentCaptor.forClass(MessageGroup.class);
    verify(userConsole).report(captured.capture());
    assertThat(captured.getValue()).isEmpty();
  }

  private static class MyNormalExecutor extends MessageCatchingExecutor<String, String> {
    public MyNormalExecutor(UserConsole userConsole, String name) {
      super(userConsole, name);
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

    ArgumentCaptor<MessageGroup> captured = ArgumentCaptor.forClass(MessageGroup.class);
    verify(userConsole).report(captured.capture());
    assertThat(Iterables.size(captured.getValue())).isEqualTo(1);
    assertThat(Iterables.get(captured.getValue(), 0).message()).isEqualTo(value);

  }

  private static class MyThrowingExecutor extends MessageCatchingExecutor<String, String> {
    public MyThrowingExecutor(UserConsole userConsole, String name) {
      super(userConsole, name);
    }

    @Override
    public String executeImpl(String arguments) {
      throw new ErrorMessageException(new Message(ERROR, arguments));
    }
  }
}
