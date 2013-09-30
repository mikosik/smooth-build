package org.smoothbuild.message.message;

import static nl.jqno.equalsverifier.Warning.NULL_FIELDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.message.message.MessageType.WARNING;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

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
    EqualsVerifier.forClass(Message.class).suppress(NULL_FIELDS).verify();
  }
}
