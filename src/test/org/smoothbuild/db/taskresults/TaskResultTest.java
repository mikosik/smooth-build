package org.smoothbuild.db.taskresults;

import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.testing.lang.type.FakeString;
import org.testory.Closure;

import com.google.common.collect.ImmutableList;

public class TaskResultTest {
  private TaskResult<SString> taskResult;
  private final FakeString sstring = new FakeString("abc");
  private final ImmutableList<Message> messages = ImmutableList.of(new Message(ERROR, ""));

  @Test
  public void null_result_is_forbidden() {
    when(newTaskResult(null, messages));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_messages_are_forbidden() {
    when(newTaskResult(new FakeString(""), null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void value_returns_result_value() throws Exception {
    given(taskResult = new TaskResult<SString>(sstring, messages));
    when(taskResult).value();
    thenReturned(sstring);
  }

  @Test
  public void messages_returns_messages() throws Exception {
    given(taskResult = new TaskResult<SString>(sstring, messages));
    when(taskResult).messages();
    thenReturned(messages);
  }

  @Test
  public void value_throws_exception_when_no_value_is_present() throws Exception {
    given(taskResult = new TaskResult<SString>(messages));
    when(taskResult).value();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void has_value_returns_true_when_value_is_present() throws Exception {
    given(taskResult = new TaskResult<SString>(sstring, messages));
    when(taskResult).hasValue();
    thenReturned(true);
  }

  @Test
  public void has_value_returns_false_when_value_is_present() throws Exception {
    given(taskResult = new TaskResult<SString>(messages));
    when(taskResult).hasValue();
    thenReturned(false);
  }

  private Closure newTaskResult(final SValue result, final ImmutableList<Message> messages) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new TaskResult<SValue>(result, messages);
      }
    };
  }
}
