package org.smoothbuild.message.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.listen.MessageType.ERROR;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;

import org.junit.Test;

public class CodeErrorTest {
  @Test
  public void type() {
    assertThat(new CodeError(codeLocation(1, 2, 3), "message").type()).isEqualTo(ERROR);
  }
}
