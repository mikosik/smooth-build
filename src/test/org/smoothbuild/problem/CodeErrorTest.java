package org.smoothbuild.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.problem.ProblemType.ERROR;

import org.junit.Test;

public class CodeErrorTest {
  @Test
  public void type() {
    assertThat(new CodeError(new CodeLocation(1, 2, 3), "message").type()).isEqualTo(ERROR);
  }
}
