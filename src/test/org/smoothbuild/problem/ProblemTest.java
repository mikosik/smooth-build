package org.smoothbuild.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.problem.ProblemType.ERROR;
import static org.smoothbuild.problem.ProblemType.WARNING;

import org.junit.Test;

public class ProblemTest {

  @Test
  public void testError() {
    SourceLocation location = mock(SourceLocation.class);
    String message = "message";

    Problem problem = new Problem(ERROR, location, message);

    assertThat(problem.type()).isEqualTo(ERROR);
    assertThat(problem.sourceLocation()).isSameAs(location);
    assertThat(problem.message()).isEqualTo(message);
  }

  @Test
  public void testWarning() {
    SourceLocation location = mock(SourceLocation.class);
    String message = "message";

    Problem problem = new Problem(WARNING, location, message);

    assertThat(problem.type()).isEqualTo(WARNING);
    assertThat(problem.sourceLocation()).isSameAs(location);
    assertThat(problem.message()).isEqualTo(message);
  }

  @Test
  public void testToString() throws Exception {
    Problem problem = new Problem(WARNING, new SourceLocation(1, 2, 3), "problem description");
    assertThat(problem.toString()).isEqualTo("WARNING[1:2-3]: problem description");
  }
}
