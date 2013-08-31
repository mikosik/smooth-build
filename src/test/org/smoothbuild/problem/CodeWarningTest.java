package org.smoothbuild.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.problem.ProblemType.WARNING;

import org.junit.Test;

public class CodeWarningTest {
  @Test
  public void type() {
    assertThat(new CodeWarning(new SourceLocation(1, 2, 3), "message").type()).isEqualTo(WARNING);
  }
}
