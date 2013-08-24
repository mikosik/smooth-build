package org.smoothbuild.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.problem.ProblemType.ERROR;

import org.junit.Test;

public class ProblemsTest {
  Problems problems = new Problems();

  @Test
  public void initiallyHasNoProblem() {
    assertThat(problems.hasAnyProblem()).isFalse();
  }

  @Test
  public void hasProblemAfterAdding() throws Exception {
    problems.report(mock(Problem.class));
    assertThat(problems.hasAnyProblem()).isTrue();
  }

  @Test
  public void toList() throws Exception {
    Problem problem1 = mock(Problem.class);
    Problem problem2 = mock(Problem.class);

    problems.report(problem1);
    problems.report(problem2);

    assertThat(problems.toList()).containsExactly(problem1, problem2);
  }

  @Test
  public void testToString() throws Exception {
    Problem problem1 = new Problem(ERROR, new SourceLocation(1, 2, 3), "description");
    Problem problem2 = new Problem(ERROR, new SourceLocation(1, 2, 3), "description");

    problems.report(problem1);
    problems.report(problem2);

    assertThat(problems.toString()).isEqualTo(
        "Reported problems:\n" + problem1.toString() + "\n" + problem2.toString() + "\n");
  }
}
