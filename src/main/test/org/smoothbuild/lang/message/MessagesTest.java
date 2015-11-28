package org.smoothbuild.lang.message;

import static java.util.Arrays.asList;
import static org.smoothbuild.lang.message.MessageType.ERROR;
import static org.smoothbuild.lang.message.MessageType.INFO;
import static org.smoothbuild.lang.message.MessageType.WARNING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class MessagesTest {
  Iterable<Message> messages;

  @Test
  public void empty_list_contains_no_errors() {
    given(messages = asList());
    when(Messages.containsErrors(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_info_messsage_contains_no_errors() {
    given(messages = asList(new Message(INFO, "")));
    when(Messages.containsErrors(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_warning_messsage_contains_no_errors() {
    given(messages = asList(new Message(WARNING, "")));
    when(Messages.containsErrors(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_error_messsage_contains_errors() {
    given(messages = asList(new Message(ERROR, "")));
    when(Messages.containsErrors(messages));
    thenReturned(true);
  }
}
