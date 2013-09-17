package org.smoothbuild.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.CodeLocation.codeLocation;
import static org.smoothbuild.message.MessageType.ERROR;

import org.junit.Test;

public class CodeErrorTest {
  @Test
  public void type() {
    assertThat(new CodeError(codeLocation(1, 2, 3), "message").type()).isEqualTo(ERROR);
  }
}
