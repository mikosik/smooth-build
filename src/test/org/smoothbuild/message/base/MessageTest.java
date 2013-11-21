package org.smoothbuild.message.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.WARNING;

import org.junit.Test;

import com.google.common.testing.EqualsTester;

public class MessageTest {

  @Test(expected = NullPointerException.class)
  public void nullMessageIsForbidden() throws Exception {
    new Message(WARNING, null);
  }

  @Test(expected = NullPointerException.class)
  public void nullTypeIsForbidden() throws Exception {
    new Message(null, "message");
  }

  @Test
  public void testError() {
    String string = "message";

    Message message = new Message(ERROR, string);

    assertThat(message.type()).isEqualTo(ERROR);
    assertThat(message.message()).isEqualTo(string);
  }

  @Test
  public void testWarning() {
    String string = "message";

    Message message = new Message(WARNING, string);

    assertThat(message.type()).isEqualTo(WARNING);
    assertThat(message.message()).isEqualTo(string);
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
