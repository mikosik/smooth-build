package org.smoothbuild.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.problem.ProblemType.WARNING;

import org.junit.Test;

public class CodeProblemTest {

  @Test(expected = NullPointerException.class)
  public void nullSourceLocationIsForbidden() throws Exception {
    new CodeProblem(WARNING, null, "message");
  }

  @Test
  public void testToString() throws Exception {
    Problem problem = new CodeProblem(WARNING, new SourceLocation(1, 2, 3), "problem description");
    assertThat(problem.toString()).isEqualTo("WARNING[1:2-3]: problem description");
  }
}
