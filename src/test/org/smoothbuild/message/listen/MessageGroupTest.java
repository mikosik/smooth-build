package org.smoothbuild.message.listen;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.message.message.MessageType.WARNING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.message.message.Message;

public class MessageGroupTest {
  String name = "name";
  Message message = new Message(ERROR, "message string");
  MessageGroup messageGroup = new MessageGroup(name);

  @Test
  public void name() throws Exception {
    given(messageGroup = new MessageGroup(name));
    when(messageGroup.name());
    thenReturned(name);
  }

  @Test
  public void added_messages_are_iterable() throws Exception {
    given(messageGroup).report(message);
    when(messageGroup);
    thenReturned(contains(message));
  }

  @Test
  public void initially_no_error_is_reported() throws Exception {
    when(messageGroup.containsErrors());
    thenReturned(false);
  }

  @Test
  public void containsErrors_returns_false_after_after_adding_warning() throws Exception {
    given(messageGroup).report(new Message(WARNING, "message"));
    when(messageGroup.containsErrors());
    thenReturned(false);
  }

  @Test
  public void containsErrors_returns_true_after_after_adding_error() throws Exception {
    given(messageGroup).report(new Message(ERROR, "message"));
    when(messageGroup.containsErrors());
    thenReturned(true);
  }

  @Test
  public void test_toString() throws Exception {
    given(messageGroup).report(message);
    when(messageGroup.toString());
    thenReturned(message.toString() + "\n");
  }
}
