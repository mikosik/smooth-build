package org.smoothbuild.parse;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.smoothbuild.problem.Problem;
import org.smoothbuild.problem.ProblemsListener;

import com.google.common.collect.Lists;

public class TestingProblemsListener implements ProblemsListener {
  private final List<Problem> list = Lists.newArrayList();

  @Override
  public void report(Problem problem) {
    list.add(problem);
  }

  public List<Problem> collected() {
    return list;
  }

  public void assertOnlyProblem(Class<? extends Problem> klass) {
    assertThat(list.size()).isEqualTo(1);
    assertThat(list.get(0)).isInstanceOf(klass);
  }
}
