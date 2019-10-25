package org.smoothbuild.task.base;

import static org.hamcrest.Matchers.not;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.testing.TestingContext;

public class OutputTest extends TestingContext {
  private Output output;
  private final Array messages = messageArrayWithOneError();
  private SString sstring;
  private SString otherSstring;

  @Test
  public void null_messages_are_forbidden() {
    given(sstring = string("abc"));
    when(() -> new Output(sstring, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void value_returns_result_value() {
    given(sstring = string("abc"));
    given(output = new Output(sstring, messages));
    when(output).result();
    thenReturned(sstring);
  }

  @Test
  public void messages_returns_messages() {
    given(sstring = string("abc"));
    given(output = new Output(sstring, messages));
    when(output).messages();
    thenReturned(messages);
  }

  @Test
  public void output_created_without_messages_has_no_messages() {
    given(sstring = string("abc"));
    given(output = new Output(sstring, emptyMessageArray()));
    when(output).messages();
    thenReturned(emptyMessageArray());
  }

  @Test
  public void value_throws_exception_when_no_value_is_present() {
    given(output = new Output(null, messages));
    when(output).result();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void has_value_returns_true_when_value_is_present() {
    given(sstring = string("abc"));
    given(output = new Output(sstring, messages));
    when(output).hasResult();
    thenReturned(true);
  }

  @Test
  public void has_value_returns_false_when_value_is_present() {
    given(output = new Output(null, messages));
    when(output).hasResult();
    thenReturned(false);
  }

  @Test
  public void outputs_with_same_return_value_and_messages_are_equal() {
    given(sstring = string("abc"));
    given(output = new Output(sstring, messages));
    when(output).equals(new Output(sstring, messages));
    thenReturned(true);
  }

  @Test
  public void outputs_with_same_return_value_and_no_messages_are_equal() {
    given(sstring = string("abc"));
    given(output = new Output(sstring, emptyMessageArray()));
    when(output).equals(new Output(sstring, emptyMessageArray()));
    thenReturned(true);
  }

  @Test
  public void outputs_with_same_message_and_no_return_value_are_equal() {
    given(output = new Output(null, messages));
    when(output).equals(new Output(null, messages));
    thenReturned(true);
  }

  @Test
  public void outputs_with_same_return_value_but_different_messages_are_not_equal() {
    given(sstring = string("abc"));
    given(output = new Output(sstring, messages));
    when(output).equals(new Output(sstring, emptyMessageArray()));
    thenReturned(false);
  }

  @Test
  public void outputs_with_different_return_values_and_same_messages_are_not_equal() {
    given(sstring = string("abc"));
    given(otherSstring = string("def"));
    given(output = new Output(sstring, messages));
    when(output).equals(new Output(otherSstring, messages));
    thenReturned(false);
  }

  @Test
  public void output_without_return_value_is_not_equal_to_output_with_result_value() {
    given(sstring = string("abc"));
    when(output = new Output(sstring, messages));
    thenReturned(not(new Output(null, messages)));
  }

  @Test
  public void identical_outputs_have_same_hash_code() {
    given(sstring = string("abc"));
    given(output = new Output(sstring, messages));
    when(output).hashCode();
    thenReturned(new Output(sstring, messages).hashCode());
  }
}
