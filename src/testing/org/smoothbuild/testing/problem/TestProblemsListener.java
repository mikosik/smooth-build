package org.smoothbuild.testing.problem;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.smoothbuild.problem.Problem;
import org.smoothbuild.problem.ProblemsListener;

import com.google.common.collect.Lists;

public class TestProblemsListener implements ProblemsListener {
  private final List<Problem> list = Lists.newArrayList();

  @Override
  public void report(Problem problem) {
    list.add(problem);
  }

  public void assertProblemsFound() {
    assertThat(list.isEmpty()).isFalse();
  }

  public void assertOnlyProblem(Class<? extends Problem> klass) {
    if (list.size() != 1) {
      throw new AssertionError("Expected one problem,\nbut got:\n" + list.toString());
    }
    assertThat(list.get(0)).isInstanceOf(klass);
  }

  public void assertNoProblems() {
    if (!list.isEmpty()) {
      StringBuilder builder = new StringBuilder("Expected zero problems, but got:\n");
      for (Problem problem : list) {
        builder.append(problem.toString());
        builder.append("\n");
      }
      throw new AssertionError(builder.toString());
    }
  }
}
