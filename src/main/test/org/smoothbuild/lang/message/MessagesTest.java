package org.smoothbuild.lang.message;

import static org.smoothbuild.lang.message.Messages.containsErrors;
import static org.smoothbuild.testing.db.values.ValueCreators.array;
import static org.smoothbuild.testing.db.values.ValueCreators.emptyMessageArray;
import static org.smoothbuild.testing.db.values.ValueCreators.errorMessage;
import static org.smoothbuild.testing.db.values.ValueCreators.infoMessage;
import static org.smoothbuild.testing.db.values.ValueCreators.warningMessage;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.lang.value.Array;

public class MessagesTest {
  private Array messages;
  private HashedDb hashedDb = new TestingHashedDb();

  @Test
  public void empty_list_contains_no_errors() {
    given(messages = emptyMessageArray());
    when(containsErrors(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_info_messsage_contains_no_errors() {
    given(messages = array(hashedDb, infoMessage(hashedDb, "info message")));
    when(containsErrors(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_warning_messsage_contains_no_errors() {
    given(messages = array(hashedDb, warningMessage(hashedDb, "warning message")));
    when(containsErrors(messages));
    thenReturned(false);
  }

  @Test
  public void list_with_error_messsage_contains_errors() {
    given(messages = array(hashedDb, errorMessage(hashedDb, "error message")));
    when(containsErrors(messages));
    thenReturned(true);
  }
}
