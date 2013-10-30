package org.smoothbuild.message.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.message.MessageType.ERROR;

import org.junit.Test;
import org.smoothbuild.testing.message.FakeCodeLocation;

public class ErrorCodeMessageTest {
  @Test
  public void type() {
    assertThat(new CodeMessage(ERROR, new FakeCodeLocation(), "message").type()).isEqualTo(ERROR);
  }
}
