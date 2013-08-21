package org.smoothbuild.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

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
}
