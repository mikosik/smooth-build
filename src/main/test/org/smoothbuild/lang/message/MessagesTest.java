package org.smoothbuild.lang.message;

import static org.smoothbuild.lang.message.Messages.containsErrors;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.testing.TestingContext;

public class MessagesTest extends TestingContext {
  private Array messages;

  @Test
  public void empty_list_contains_no_errors() {
    given(messages = emptyMessageArray());
    when(containsErrors(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_info_messsage_contains_no_errors() {
    given(messages = array(infoMessage("info message")));
    when(containsErrors(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_warning_messsage_contains_no_errors() {
    given(messages = array(warningMessage("warning message")));
    when(containsErrors(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_error_messsage_contains_errors() {
    given(messages = array(errorMessage("error message")));
    when(containsErrors(messages));
    thenReturned(true);
  }
}
