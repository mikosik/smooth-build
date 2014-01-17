package org.smoothbuild.message.listen;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.FATAL;
import static org.smoothbuild.message.base.MessageType.INFO;
import static org.smoothbuild.message.base.MessageType.SUGGESTION;
import static org.smoothbuild.message.base.MessageType.WARNING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.message.base.Message;

public class LoggedMessagesTest {
  String name = "name";
  Message info = new Message(INFO, "message");
  Message suggestion = new Message(SUGGESTION, "message");
  Message warning = new Message(WARNING, "message");
  Message error = new Message(ERROR, "message");
  Message fatal = new Message(FATAL, "message");

  LoggedMessages loggedMessages = new LoggedMessages();

  @Test
  public void added_messages_are_iterable() throws Exception {
    given(loggedMessages).log(error);
    when(loggedMessages);
    thenReturned(contains(error));
  }

  // containsMessages()

  @Test
  public void contains_no_messages_initially() throws Exception {
    when(!loggedMessages.isEmpty());
    thenReturned(false);
  }

  @Test
  public void contains_messages_when_info_has_been_added() throws Exception {
    given(loggedMessages).log(info);
    when(!loggedMessages.isEmpty());
    thenReturned(true);
  }

  @Test
  public void contains_messages_when_suggestion_has_been_added() throws Exception {
    given(loggedMessages).log(suggestion);
    when(!loggedMessages.isEmpty());
    thenReturned(true);
  }

  @Test
  public void contains_messages_when_warning_has_been_added() throws Exception {
    given(loggedMessages).log(warning);
    when(!loggedMessages.isEmpty());
    thenReturned(true);
  }

  @Test
  public void contains_messages_when_error_has_been_added() throws Exception {
    given(loggedMessages).log(error);
    when(!loggedMessages.isEmpty());
    thenReturned(true);
  }

  @Test
  public void contains_messages_when_fatal_has_been_added() throws Exception {
    given(loggedMessages).log(fatal);
    when(!loggedMessages.isEmpty());
    thenReturned(true);
  }

  // containsProblems()

  @Test
  public void contains_no_problem_initially() throws Exception {
    when(loggedMessages.containsProblems());
    thenReturned(false);
  }

  @Test
  public void contains_no_problem_after_adding_info() throws Exception {
    given(loggedMessages).log(info);
    when(loggedMessages.containsProblems());
    thenReturned(false);
  }

  @Test
  public void contains_no_problem_after_adding_suggeestion() throws Exception {
    given(loggedMessages).log(suggestion);
    when(loggedMessages.containsProblems());
    thenReturned(false);
  }

  @Test
  public void contains_no_problems_after_adding_warning() throws Exception {
    given(loggedMessages).log(warning);
    when(loggedMessages.containsProblems());
    thenReturned(false);
  }

  @Test
  public void contains_problems_after_adding_error() throws Exception {
    given(loggedMessages).log(error);
    when(loggedMessages.containsProblems());
    thenReturned(true);
  }

  @Test
  public void contains_problems_after_adding_fatal() throws Exception {
    given(loggedMessages).log(fatal);
    when(loggedMessages.containsProblems());
    thenReturned(true);
  }

  // failIfContainsProblems()

  @Test
  public void failIfContainsProblems_throws_exception_when_info_was_logged() throws Exception {
    given(loggedMessages).log(info);
    when(loggedMessages).failIfContainsProblems();
    thenReturned();
  }

  @Test
  public void failIfContainsProblems_throws_exception_when_suggestion_was_logged() throws Exception {
    given(loggedMessages).log(suggestion);
    when(loggedMessages).failIfContainsProblems();
    thenReturned();
  }

  @Test
  public void failIfContainsProblems_does_nothing_when_only_warning_was_logged() throws Exception {
    given(loggedMessages).log(warning);
    when(loggedMessages).failIfContainsProblems();
    thenReturned();
  }

  @Test
  public void failIfContainsProblems_throws_exception_when_error_was_logged() throws Exception {
    given(loggedMessages).log(error);
    when(loggedMessages).failIfContainsProblems();
    thenThrown(PhaseFailedException.class);
  }

  @Test
  public void failIfContainsProblems_throws_exception_when_fatal_was_logged() throws Exception {
    given(loggedMessages).log(fatal);
    when(loggedMessages).failIfContainsProblems();
    thenThrown(PhaseFailedException.class);
  }

  // toString()

  @Test
  public void test_toString() throws Exception {
    given(loggedMessages).log(error);
    when(loggedMessages.toString());
    thenReturned(error.toString() + "\n");
  }
}
