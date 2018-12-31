package org.smoothbuild.task.base;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.List;

import org.junit.Test;
import org.smoothbuild.db.values.TestingValuesDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.message.MessagesDb;
import org.smoothbuild.lang.message.TestingMessagesDb;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.util.Lists;

public class OutputTest {
  private final ValuesDb valuesDb = new TestingValuesDb();
  private final MessagesDb messagesDb = new TestingMessagesDb();
  private Output output;
  private final List<Message> messages = list(messagesDb.error(""));
  private SString sstring;
  private SString otherSstring;

  @Test
  public void null_messages_are_forbidden() {
    given(sstring = valuesDb.string("abc"));
    when(() -> new Output(sstring, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void value_returns_result_value() throws Exception {
    given(sstring = valuesDb.string("abc"));
    given(output = new Output(sstring, messages));
    when(output).result();
    thenReturned(sstring);
  }

  @Test
  public void messages_returns_messages() throws Exception {
    given(sstring = valuesDb.string("abc"));
    given(output = new Output(sstring, messages));
    when(output).messages();
    thenReturned(messages);
  }

  @Test
  public void output_created_without_messages_has_no_messages() throws Exception {
    given(sstring = valuesDb.string("abc"));
    given(output = new Output(sstring));
    when(output).messages();
    thenReturned(empty());
  }

  @Test
  public void output_created_without_messages_is_cacheable() throws Exception {
    given(sstring = valuesDb.string("abc"));
    given(output = new Output(sstring));
    when(() -> output.isCacheable());
    thenReturned(true);
  }

  @Test
  public void output_created_with_messages_is_cacheable() throws Exception {
    given(sstring = valuesDb.string("abc"));
    given(output = new Output(sstring, messages));
    when(() -> output.isCacheable());
    thenReturned(true);
  }

  @Test
  public void output_created_as_not_cacheable_is_not_cacheable() throws Exception {
    given(sstring = valuesDb.string("abc"));
    given(output = new Output(sstring, messages, false));
    when(() -> output.isCacheable());
    thenReturned(false);
  }

  @Test
  public void value_throws_exception_when_no_value_is_present() throws Exception {
    given(output = new Output(messages));
    when(output).result();
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void has_value_returns_true_when_value_is_present() throws Exception {
    given(sstring = valuesDb.string("abc"));
    given(output = new Output(sstring, messages));
    when(output).hasResult();
    thenReturned(true);
  }

  @Test
  public void has_value_returns_false_when_value_is_present() throws Exception {
    given(output = new Output(messages));
    when(output).hasResult();
    thenReturned(false);
  }

  @Test
  public void outputs_with_same_return_value_and_messages_are_equal() throws Exception {
    given(sstring = valuesDb.string("abc"));
    given(output = new Output(sstring, messages));
    when(output).equals(new Output(sstring, messages));
    thenReturned(true);
  }

  @Test
  public void outputs_with_same_return_value_and_no_messages_are_equal() throws Exception {
    given(sstring = valuesDb.string("abc"));
    given(output = new Output(sstring));
    when(output).equals(new Output(sstring));
    thenReturned(true);
  }

  @Test
  public void outputs_with_same_message_and_no_return_value_are_equal() throws Exception {
    given(output = new Output(messages));
    when(output).equals(new Output(messages));
    thenReturned(true);
  }

  @Test
  public void outputs_with_same_return_value_but_different_messages_are_not_equal()
      throws Exception {
    given(sstring = valuesDb.string("abc"));
    given(output = new Output(sstring, messages));
    when(output).equals(new Output(sstring, Lists.<Message>list()));
    thenReturned(false);
  }

  @Test
  public void outputs_with_different_return_values_and_same_messages_are_not_equal()
      throws Exception {
    given(sstring = valuesDb.string("abc"));
    given(otherSstring = valuesDb.string("def"));
    given(output = new Output(sstring, messages));
    when(output).equals(new Output(otherSstring, messages));
    thenReturned(false);
  }

  @Test
  public void output_without_return_value_is_not_equal_to_output_with_result_value()
      throws Exception {
    given(sstring = valuesDb.string("abc"));
    when(output = new Output(sstring, messages));
    thenReturned(not(new Output(messages)));
  }

  @Test
  public void identical_outputs_have_same_hash_code() throws Exception {
    given(sstring = valuesDb.string("abc"));
    given(output = new Output(sstring, messages));
    when(output).hashCode();
    thenReturned(new Output(sstring, messages).hashCode());
  }
}
