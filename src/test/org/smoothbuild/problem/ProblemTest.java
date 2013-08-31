package org.smoothbuild.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.problem.ProblemType.ERROR;
import static org.smoothbuild.problem.ProblemType.WARNING;

import org.junit.Test;

public class ProblemTest {

  @Test(expected = NullPointerException.class)
  public void nullMessageIsForbidden() throws Exception {
    new Problem(WARNING, null);
  }

  @Test(expected = NullPointerException.class)
  public void nullTypeIsForbidden() throws Exception {
    new Problem(null, "message");
  }

  @Test
  public void testError() {
    String message = "message";

    Problem problem = new Problem(ERROR, message);

    assertThat(problem.type()).isEqualTo(ERROR);
    assertThat(problem.message()).isEqualTo(message);
  }

  @Test
  public void testWarning() {
    String message = "message";

    Problem problem = new Problem(WARNING, message);

    assertThat(problem.type()).isEqualTo(WARNING);
    assertThat(problem.message()).isEqualTo(message);
  }
}
