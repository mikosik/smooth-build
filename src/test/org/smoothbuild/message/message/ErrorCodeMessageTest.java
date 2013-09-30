package org.smoothbuild.message.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.message.message.MessageType.ERROR;

import org.junit.Test;

public class ErrorCodeMessageTest {
  @Test
  public void type() {
    assertThat(new CodeMessage(ERROR, codeLocation(1, 2, 3), "message").type()).isEqualTo(ERROR);
  }
}
