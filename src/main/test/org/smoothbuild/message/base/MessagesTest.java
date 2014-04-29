package org.smoothbuild.message.base;

import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.FATAL;
import static org.smoothbuild.message.base.MessageType.INFO;
import static org.smoothbuild.message.base.MessageType.SUGGESTION;
import static org.smoothbuild.message.base.MessageType.WARNING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class MessagesTest {
  Iterable<Message> messages;

  @Test
  public void empty_list_contains_no_problems() {
    given(messages = ImmutableList.of());
    when(Messages.containsProblems(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_info_messsage_contains_no_problems() {
    given(messages = ImmutableList.of(new Message(INFO, "")));
    when(Messages.containsProblems(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_warning_messsage_contains_no_problems() {
    given(messages = ImmutableList.of(new Message(WARNING, "")));
    when(Messages.containsProblems(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_suggestion_messsage_contains_no_problems() {
    given(messages = ImmutableList.of(new Message(SUGGESTION, "")));
    when(Messages.containsProblems(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_error_messsage_contains_no_problems() {
    given(messages = ImmutableList.of(new Message(ERROR, "")));
    when(Messages.containsProblems(messages));
    thenReturned(true);
  }

  @Test
  public void list_with_fatal_messsage_contains_no_problems() {
    given(messages = ImmutableList.of(new Message(FATAL, "")));
    when(Messages.containsProblems(messages));
    thenReturned(true);
  }
}
