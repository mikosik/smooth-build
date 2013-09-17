package org.smoothbuild.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.CodeLocation.codeLocation;
import static org.smoothbuild.message.MessageType.WARNING;

import org.junit.Test;

public class CodeWarningTest {
  @Test
  public void type() {
    assertThat(new CodeWarning(codeLocation(1, 2, 3), "message").type()).isEqualTo(WARNING);
  }
}
