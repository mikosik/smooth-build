package org.smoothbuild.lang.message;

import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class MessagesTest {
  private final MessagesDb messagesDb = new TestingMessagesDb();
  private Iterable<Message> messages;

  @Test
  public void empty_list_contains_no_errors() {
    given(messages = list());
    when(Messages.containsErrors(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_info_messsage_contains_no_errors() {
    given(messages = list(messagesDb.info("")));
    when(Messages.containsErrors(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_warning_messsage_contains_no_errors() {
    given(messages = list(messagesDb.warning("")));
    when(Messages.containsErrors(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_error_messsage_contains_errors() {
    given(messages = list(messagesDb.error("")));
    when(Messages.containsErrors(messages));
    thenReturned(true);
  }
}
