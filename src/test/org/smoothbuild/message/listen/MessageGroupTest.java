package org.smoothbuild.message.listen;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.message.message.MessageType.WARNING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.message.message.Message;
import org.testory.common.Closure;

public class MessageGroupTest {
  String name = "name";
  Message warning = new Message(WARNING, "message");
  Message error = new Message(ERROR, "message");

  MessageGroup messageGroup = new MessageGroup(name);

  @Test
  public void null_name_is_forbidden() throws Exception {
    when($messageGroup(null));
    thenThrown(NullPointerException.class);
  }

  private Closure $messageGroup(final String name) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new MessageGroup(name);
      }
    };
  }

  @Test
  public void name() throws Exception {
    given(messageGroup = new MessageGroup(name));
    when(messageGroup.name());
    thenReturned(name);
  }

  @Test
  public void added_messages_are_iterable() throws Exception {
    given(messageGroup).report(error);
    when(messageGroup);
    thenReturned(contains(error));
  }

  @Test
  public void initially_no_error_is_reported() throws Exception {
    when(messageGroup.containsErrors());
    thenReturned(false);
  }

  @Test
  public void containsMessages_returns_false_when_nothing_has_been_added() throws Exception {
    when(messageGroup.containsMessages());
    thenReturned(false);
  }

  @Test
  public void containsMessages_returns_true_when_warning_has_been_added() throws Exception {
    given(messageGroup).report(warning);
    when(messageGroup.containsMessages());
    thenReturned(true);
  }

  @Test
  public void containsMessages_returns_true_when_error_has_been_added() throws Exception {
    given(messageGroup).report(error);
    when(messageGroup.containsMessages());
    thenReturned(true);
  }

  @Test
  public void containsErrors_returns_false_after_after_adding_warning() throws Exception {
    given(messageGroup).report(warning);
    when(messageGroup.containsErrors());
    thenReturned(false);
  }

  @Test
  public void containsErrors_returns_true_after_after_adding_error() throws Exception {
    given(messageGroup).report(error);
    when(messageGroup.containsErrors());
    thenReturned(true);
  }

  @Test
  public void failIfContainsError_throws_exception_when_errors_were_reported() throws Exception {
    given(messageGroup).report(error);
    when(messageGroup).failIfContainsErrors();
    thenThrown(PhaseFailedException.class);
  }

  @Test
  public void failIfContainsError_does_nothing_when_only_warning_was_reported() throws Exception {
    given(messageGroup).report(warning);
    when(messageGroup).failIfContainsErrors();
    thenReturned();
  }

  @Test
  public void test_toString() throws Exception {
    given(messageGroup).report(error);
    when(messageGroup.toString());
    thenReturned(error.toString() + "\n");
  }
}
