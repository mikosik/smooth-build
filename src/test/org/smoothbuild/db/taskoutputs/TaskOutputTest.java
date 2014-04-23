package org.smoothbuild.db.taskoutputs;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.taskoutputs.TaskOutput;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.testory.Closure;

import com.google.common.collect.ImmutableList;

public class TaskOutputTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private TaskOutput<SString> taskOutput;
  private final ImmutableList<Message> messages = ImmutableList.of(new Message(ERROR, ""));
  private SString sstring;
  private SString otherSstring;

  @Test
  public void null_result_is_forbidden() {
    when(newTaskOutput(null, messages));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_messages_are_forbidden() {
    given(sstring = objectsDb.string("abc"));
    when(newTaskOutput(sstring, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void value_returns_result_value() throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(taskOutput = new TaskOutput<>(sstring, messages));
    when(taskOutput).returnValue();
    thenReturned(sstring);
  }

  @Test
  public void messages_returns_messages() throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(taskOutput = new TaskOutput<>(sstring, messages));
    when(taskOutput).messages();
    thenReturned(messages);
  }

  @Test
  public void task_result_created_without_messages_has_no_messages() throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(taskOutput = new TaskOutput<>(sstring));
    when(taskOutput).messages();
    thenReturned(empty());
  }

  @Test
  public void value_throws_exception_when_no_value_is_present() throws Exception {
    given(taskOutput = new TaskOutput<>(messages));
    when(taskOutput).returnValue();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void has_value_returns_true_when_value_is_present() throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(taskOutput = new TaskOutput<>(sstring, messages));
    when(taskOutput).hasReturnValue();
    thenReturned(true);
  }

  @Test
  public void has_value_returns_false_when_value_is_present() throws Exception {
    given(taskOutput = new TaskOutput<>(messages));
    when(taskOutput).hasReturnValue();
    thenReturned(false);
  }

  @Test
  public void task_results_with_same_return_value_and_messages_are_equal() throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(taskOutput = new TaskOutput<>(sstring, messages));
    when(taskOutput).equals(new TaskOutput<>(sstring, messages));
    thenReturned(true);
  }

  @Test
  public void task_results_with_same_return_value_and_no_messages_are_equal() throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(taskOutput = new TaskOutput<>(sstring));
    when(taskOutput).equals(new TaskOutput<>(sstring));
    thenReturned(true);
  }

  @Test
  public void task_results_with_same_message_and_no_return_value_are_equal() throws Exception {
    given(taskOutput = new TaskOutput<>(messages));
    when(taskOutput).equals(new TaskOutput<>(messages));
    thenReturned(true);
  }

  @Test
  public void task_results_with_same_return_value_but_different_messages_are_not_equal()
      throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(taskOutput = new TaskOutput<>(sstring, messages));
    when(taskOutput).equals(new TaskOutput<>(sstring, ImmutableList.<Message> of()));
    thenReturned(false);
  }

  @Test
  public void task_results_with_different_return_values_and_same_messages_are_not_equal()
      throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(otherSstring = objectsDb.string("def"));
    given(taskOutput = new TaskOutput<>(sstring, messages));
    when(taskOutput).equals(new TaskOutput<>(otherSstring, messages));
    thenReturned(false);
  }

  @Test
  public void task_result_without_return_value_is_not_equal_to_task_result_with_result_value()
      throws Exception {
    given(sstring = objectsDb.string("abc"));
    when(taskOutput = new TaskOutput<>(sstring, messages));
    thenReturned(not(new TaskOutput<>(messages)));
  }

  @Test
  public void identical_task_results_have_same_hash_code() throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(taskOutput = new TaskOutput<>(sstring, messages));
    when(taskOutput).hashCode();
    thenReturned(new TaskOutput<>(sstring, messages).hashCode());
  }

  private Closure newTaskOutput(final SValue result, final ImmutableList<Message> messages) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new TaskOutput<SValue>(result, messages);
      }
    };
  }
}
