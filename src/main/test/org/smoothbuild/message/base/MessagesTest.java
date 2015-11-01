package org.smoothbuild.message.base;

import static java.util.Arrays.asList;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.INFO;
import static org.smoothbuild.message.base.MessageType.SUGGESTION;
import static org.smoothbuild.message.base.MessageType.WARNING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class MessagesTest {
  Iterable<Message> messages;

  @Test
  public void empty_list_contains_no_problems() {
    given(messages = asList());
    when(Messages.containsProblems(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_info_messsage_contains_no_problems() {
    given(messages = asList(new Message(INFO, "")));
    when(Messages.containsProblems(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_warning_messsage_contains_no_problems() {
    given(messages = asList(new Message(WARNING, "")));
    when(Messages.containsProblems(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_suggestion_messsage_contains_no_problems() {
    given(messages = asList(new Message(SUGGESTION, "")));
    when(Messages.containsProblems(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_error_messsage_contains_problems() {
    given(messages = asList(new Message(ERROR, "")));
    when(Messages.containsProblems(messages));
    thenReturned(true);
  }
}
