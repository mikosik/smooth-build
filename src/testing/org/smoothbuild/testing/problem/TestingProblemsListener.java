package org.smoothbuild.testing.problem;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.smoothbuild.problem.Problem;
import org.smoothbuild.problem.Problems;

public class TestingProblemsListener extends Problems {
  public void assertOnlyProblem(Class<? extends Problem> klass) {
    List<Problem> list = this.toList();
    assertThat(list.size()).isEqualTo(1);
    assertThat(list.get(0)).isInstanceOf(klass);
  }

  public void assertNoProblems() {
    if (hasAnyProblem()) {
      throw new AssertionError("Expected zero problems,\nbut got:\n" + toString());
    }
  }
}
