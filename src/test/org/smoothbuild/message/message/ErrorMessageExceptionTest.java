package org.smoothbuild.message.message;

import static org.hamcrest.Matchers.sameInstance;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;

public class ErrorMessageExceptionTest {
  Error errorMessage;
  ErrorMessageException exception;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void test() {
    given(exception = new ErrorMessageException(errorMessage));
    when(exception).error();
    thenReturned(sameInstance(errorMessage));
  }
}
