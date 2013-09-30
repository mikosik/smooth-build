package org.smoothbuild.message.message;

import static org.hamcrest.Matchers.sameInstance;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class ErrorMessageExceptionTest {
  Message errorMessage = new Message(ERROR, "");
  ErrorMessageException exception;

  @Test
  public void test() {
    given(exception = new ErrorMessageException(errorMessage));
    when(exception).errorMessage();
    thenReturned(sameInstance(errorMessage));
  }
}
