package org.smoothbuild.problem;

import java.util.List;

import com.google.common.collect.Lists;

public class Problems implements ProblemsListener {
  private final List<Problem> list = Lists.newArrayList();

  @Override
  public void report(Problem problem) {
    list.add(problem);
  }

  public boolean hasAnyProblem() {
    return !list.isEmpty();
  }
}
