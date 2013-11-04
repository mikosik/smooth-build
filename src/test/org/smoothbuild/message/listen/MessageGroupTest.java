package org.smoothbuild.message.listen;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.message.message.MessageType.FATAL;
import static org.smoothbuild.message.message.MessageType.INFO;
import static org.smoothbuild.message.message.MessageType.SUGGESTION;
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
  Message info = new Message(INFO, "message");
  Message suggestion = new Message(SUGGESTION, "message");
  Message warning = new Message(WARNING, "message");
  Message error = new Message(ERROR, "message");
  Message fatal = new Message(FATAL, "message");

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

  // containsMessages()

  @Test
  public void contains_no_messages_initially() throws Exception {
    when(messageGroup.containsMessages());
    thenReturned(false);
  }

  @Test
  public void contains_messages_when_info_has_been_added() throws Exception {
    given(messageGroup).report(info);
    when(messageGroup.containsMessages());
    thenReturned(true);
  }

  @Test
  public void contains_messages_when_suggestion_has_been_added() throws Exception {
    given(messageGroup).report(suggestion);
    when(messageGroup.containsMessages());
    thenReturned(true);
  }

  @Test
  public void contains_messages_when_warning_has_been_added() throws Exception {
    given(messageGroup).report(warning);
    when(messageGroup.containsMessages());
    thenReturned(true);
  }

  @Test
  public void contains_messages_when_error_has_been_added() throws Exception {
    given(messageGroup).report(error);
    when(messageGroup.containsMessages());
    thenReturned(true);
  }

  @Test
  public void contains_messages_when_fatal_has_been_added() throws Exception {
    given(messageGroup).report(fatal);
    when(messageGroup.containsMessages());
    thenReturned(true);
  }

  // containsProblems()

  @Test
  public void contains_no_problem_initially() throws Exception {
    when(messageGroup.containsProblems());
    thenReturned(false);
  }

  @Test
  public void contains_no_problem_after_adding_info() throws Exception {
    given(messageGroup).report(info);
    when(messageGroup.containsProblems());
    thenReturned(false);
  }

  @Test
  public void contains_no_problem_after_adding_suggeestion() throws Exception {
    given(messageGroup).report(suggestion);
    when(messageGroup.containsProblems());
    thenReturned(false);
  }

  @Test
  public void contains_no_problems_after_adding_warning() throws Exception {
    given(messageGroup).report(warning);
    when(messageGroup.containsProblems());
    thenReturned(false);
  }

  @Test
  public void contains_problems_after_adding_error() throws Exception {
    given(messageGroup).report(error);
    when(messageGroup.containsProblems());
    thenReturned(true);
  }

  @Test
  public void contains_problems_after_adding_fatal() throws Exception {
    given(messageGroup).report(fatal);
    when(messageGroup.containsProblems());
    thenReturned(true);
  }

  // failIfContainsProblems()

  @Test
  public void failIfContainsProblems_throws_exception_when_info_was_reported() throws Exception {
    given(messageGroup).report(info);
    when(messageGroup).failIfContainsProblems();
    thenReturned();
  }

  @Test
  public void failIfContainsProblems_throws_exception_when_suggestion_was_reported()
      throws Exception {
    given(messageGroup).report(suggestion);
    when(messageGroup).failIfContainsProblems();
    thenReturned();
  }

  @Test
  public void failIfContainsProblems_does_nothing_when_only_warning_was_reported() throws Exception {
    given(messageGroup).report(warning);
    when(messageGroup).failIfContainsProblems();
    thenReturned();
  }

  @Test
  public void failIfContainsProblems_throws_exception_when_error_was_reported() throws Exception {
    given(messageGroup).report(error);
    when(messageGroup).failIfContainsProblems();
    thenThrown(PhaseFailedException.class);
  }

  @Test
  public void failIfContainsProblems_throws_exception_when_fatal_was_reported() throws Exception {
    given(messageGroup).report(fatal);
    when(messageGroup).failIfContainsProblems();
    thenThrown(PhaseFailedException.class);
  }

  // toString()

  @Test
  public void test_toString() throws Exception {
    given(messageGroup).report(error);
    when(messageGroup.toString());
    thenReturned(error.toString() + "\n");
  }
}
