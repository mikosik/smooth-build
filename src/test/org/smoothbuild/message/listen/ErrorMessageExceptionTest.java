package org.smoothbuild.message.listen;

import static org.hamcrest.Matchers.sameInstance;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.INFO;
import static org.smoothbuild.message.base.MessageType.SUGGESTION;
import static org.smoothbuild.message.base.MessageType.WARNING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.message.base.Message;

public class ErrorMessageExceptionTest {
  Message errorMessage = new Message(ERROR, "");
  ErrorMessageException exception;

  @Test
  public void test() {
    given(exception = new ErrorMessageException(errorMessage));
    when(exception).errorMessage();
    thenReturned(sameInstance(errorMessage));
  }

  @Test(expected = IllegalArgumentException.class)
  public void cannot_create_error_message_exception_from_warning() throws Exception {
    new ErrorMessageException(new Message(WARNING, "message"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void cannot_create_error_message_exception_from_suggestion() throws Exception {
    new ErrorMessageException(new Message(SUGGESTION, "message"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void cannot_create_error_message_exception_from_info() throws Exception {
    new ErrorMessageException(new Message(INFO, "message"));
  }
}
