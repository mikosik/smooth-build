package org.smoothbuild.message.base;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.WARNING;

import org.junit.Test;

import com.google.common.testing.EqualsTester;

public class MessageTest {
  String messageString = "message";

  @Test(expected = NullPointerException.class)
  public void nullMessageIsForbidden() throws Exception {
    new Message(WARNING, null);
  }

  @Test(expected = NullPointerException.class)
  public void nullTypeIsForbidden() throws Exception {
    new Message(null, messageString);
  }

  @Test
  public void testError() {
    Message message = new Message(ERROR, messageString);

    assertThat(message.type()).isEqualTo(ERROR);
    assertThat(message.message()).isEqualTo(messageString);
  }

  @Test
  public void testWarning() {
    Message message = new Message(WARNING, messageString);

    assertThat(message.type()).isEqualTo(WARNING);
    assertThat(message.message()).isEqualTo(messageString);
  }

  @Test
  public void to_string() throws Exception {
    Message message = new Message(ERROR, messageString);
    assertThat(message.toString()).isEqualTo("ERROR: " + messageString);
  }

  @Test
  public void to_string_with_cause() throws Exception {
    Throwable throwable = new Throwable();
    Message message = new Message(ERROR, "message", throwable);
    assertThat(message.toString()).isEqualTo(
        "ERROR: " + messageString + "\n" + getStackTraceAsString(throwable));
  }

  @Test
  public void equalsAndHashCode() throws Exception {
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
