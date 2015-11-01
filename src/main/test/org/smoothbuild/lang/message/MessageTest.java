package org.smoothbuild.lang.message;

import static org.smoothbuild.lang.message.MessageType.ERROR;
import static org.smoothbuild.lang.message.MessageType.WARNING;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;

import com.google.common.testing.EqualsTester;

public class MessageTest {
  private String messageString;
  private Message message;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test(expected = NullPointerException.class)
  public void null_message_is_forbidden() throws Exception {
    new Message(WARNING, null);
  }

  @Test(expected = NullPointerException.class)
  public void null_type_is_forbidden() throws Exception {
    new Message(null, messageString);
  }

  @Test
  public void test_error() {
    when(message = new Message(ERROR, messageString));
    thenEqual(message.type(), ERROR);
    thenEqual(message.message(), messageString);
  }

  @Test
  public void test_warning() {
    when(message = new Message(WARNING, messageString));
    thenEqual(message.type(), WARNING);
    thenEqual(message.message(), messageString);
  }

  @Test
  public void to_string() throws Exception {
    given(message = new Message(ERROR, messageString));
    when(message.toString());
    thenReturned(ERROR.name() + ": " + messageString);
  }

  @Test
  public void equals_and_hash_code() throws Exception {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(new Message(WARNING, "equal"), new Message(WARNING, "equal"));
    for (MessageType type : MessageType.values()) {
      tester.addEqualityGroup(new Message(type, "message A"));
    }
    for (MessageType type : MessageType.values()) {
      tester.addEqualityGroup(new Message(type, "message B"));
    }

    tester.testEquals();
  }
}
