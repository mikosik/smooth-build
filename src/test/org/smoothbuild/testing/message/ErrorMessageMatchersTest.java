package org.smoothbuild.testing.message;

import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.testing.message.ErrorMessageMatchers.containsInstanceOf;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.message.message.Message;

public class ErrorMessageMatchersTest {
  Matcher<ErrorMessageException> matcher;
  ErrorMessageException message;
  StringDescription description;

  @Test
  public void null_is_not_matched() {
    given(matcher = containsInstanceOf(Message.class));
    when(matcher.matches(null));
    thenReturned(false);
  }

  @Test
  public void instance_of_wrong_type_is_not_matched() {
    given(matcher = containsInstanceOf(Message.class));
    when(matcher.matches("string"));
    thenReturned(false);
  }

  @Test
  public void exception_containing_message_of_exact_type_is_matched() {
    given(matcher = containsInstanceOf(Message.class));
    given(message = new ErrorMessageException(new Message(ERROR, "message")));
    when(matcher.matches(message));
    thenReturned(true);
  }

  @Test
  public void exception_containing_message_of_subclass_type_is_matched() {
    given(matcher = containsInstanceOf(Message.class));
    given(message = new ErrorMessageException(new MyMessage()));
    when(matcher.matches(message));
    thenReturned(true);
  }

  @Test
  public void exception_containing_message_of_wrong_type_is_not_matched() {
    given(matcher = containsInstanceOf(MyMessage.class));
    given(message = new ErrorMessageException(new Message(ERROR, "message")));
    when(matcher.matches(message));
    thenReturned(false);
  }

  @Test
  public void test_description() {
    given(matcher = containsInstanceOf(MyMessage.class));
    given(description = new StringDescription());
    given(matcher).describeTo(description);
    when(description.toString());
    thenReturned(ErrorMessageException.class.getSimpleName() + " containing "
        + MyMessage.class.getSimpleName());
  }

  private static class MyMessage extends Message {
    public MyMessage() {
      super(ERROR, "message");
    }
  }
}
