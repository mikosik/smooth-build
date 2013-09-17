package org.smoothbuild.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.problem.CodeLocation.codeLocation;
import static org.smoothbuild.problem.MessageType.WARNING;

import org.junit.Test;

public class CodeWarningTest {
  @Test
  public void type() {
    assertThat(new CodeWarning(codeLocation(1, 2, 3), "message").type()).isEqualTo(WARNING);
  }
}
