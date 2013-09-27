package org.smoothbuild.message.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.listen.MessageType.WARNING;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;

import org.junit.Test;

public class CodeWarningTest {
  @Test
  public void type() {
    assertThat(new CodeWarning(codeLocation(1, 2, 3), "message").type()).isEqualTo(WARNING);
  }
}
