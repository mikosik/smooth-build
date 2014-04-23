package org.smoothbuild.db.taskresults;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.testory.Closure;

import com.google.common.collect.ImmutableList;

public class TaskResultTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private TaskResult<SString> taskResult;
  private final ImmutableList<Message> messages = ImmutableList.of(new Message(ERROR, ""));
  private SString sstring;
  private SString otherSstring;

  @Test
  public void null_result_is_forbidden() {
    when(newTaskResult(null, messages));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_messages_are_forbidden() {
    given(sstring = objectsDb.string("abc"));
    when(newTaskResult(sstring, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void value_returns_result_value() throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(taskResult = new TaskResult<>(sstring, messages));
    when(taskResult).returnValue();
    thenReturned(sstring);
  }

  @Test
  public void messages_returns_messages() throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(taskResult = new TaskResult<>(sstring, messages));
    when(taskResult).messages();
    thenReturned(messages);
  }

  @Test
  public void task_result_created_without_messages_has_no_messages() throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(taskResult = new TaskResult<>(sstring));
    when(taskResult).messages();
    thenReturned(empty());
  }

  @Test
  public void value_throws_exception_when_no_value_is_present() throws Exception {
    given(taskResult = new TaskResult<>(messages));
    when(taskResult).returnValue();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void has_value_returns_true_when_value_is_present() throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(taskResult = new TaskResult<>(sstring, messages));
    when(taskResult).hasReturnValue();
    thenReturned(true);
  }

  @Test
  public void has_value_returns_false_when_value_is_present() throws Exception {
    given(taskResult = new TaskResult<>(messages));
    when(taskResult).hasReturnValue();
    thenReturned(false);
  }

  @Test
  public void task_results_with_same_return_value_and_messages_are_equal() throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(taskResult = new TaskResult<>(sstring, messages));
    when(taskResult).equals(new TaskResult<>(sstring, messages));
    thenReturned(true);
  }

  @Test
  public void task_results_with_same_return_value_and_no_messages_are_equal() throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(taskResult = new TaskResult<>(sstring));
    when(taskResult).equals(new TaskResult<>(sstring));
    thenReturned(true);
  }

  @Test
  public void task_results_with_same_message_and_no_return_value_are_equal() throws Exception {
    given(taskResult = new TaskResult<>(messages));
    when(taskResult).equals(new TaskResult<>(messages));
    thenReturned(true);
  }

  @Test
  public void task_results_with_same_return_value_but_different_messages_are_not_equal()
      throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(taskResult = new TaskResult<>(sstring, messages));
    when(taskResult).equals(new TaskResult<>(sstring, ImmutableList.<Message> of()));
    thenReturned(false);
  }

  @Test
  public void task_results_with_different_return_values_and_same_messages_are_not_equal()
      throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(otherSstring = objectsDb.string("def"));
    given(taskResult = new TaskResult<>(sstring, messages));
    when(taskResult).equals(new TaskResult<>(otherSstring, messages));
    thenReturned(false);
  }

  @Test
  public void task_result_without_return_value_is_not_equal_to_task_result_with_result_value()
      throws Exception {
    given(sstring = objectsDb.string("abc"));
    when(taskResult = new TaskResult<>(sstring, messages));
    thenReturned(not(new TaskResult<>(messages)));
  }

  @Test
  public void identical_task_results_have_same_hash_code() throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(taskResult = new TaskResult<>(sstring, messages));
    when(taskResult).hashCode();
    thenReturned(new TaskResult<>(sstring, messages).hashCode());
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
