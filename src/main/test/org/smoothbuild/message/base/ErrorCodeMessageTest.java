package org.smoothbuild.message.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.smoothbuild.message.base.MessageType.ERROR;

import org.junit.Test;

public class ErrorCodeMessageTest {
  @Test
  public void type() {
    assertThat(new CodeMessage(ERROR, codeLocation(1), "message").type()).isEqualTo(ERROR);
  }
}
