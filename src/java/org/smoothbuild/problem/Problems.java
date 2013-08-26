package org.smoothbuild.problem;

import java.util.List;

import javax.inject.Singleton;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@Singleton
public class Problems implements ProblemsListener {
  private final List<Problem> list = Lists.newArrayList();

  @Override
  public void report(Problem problem) {
    list.add(problem);
  }

  @Override
  public boolean hasAnyProblem() {
    return !list.isEmpty();
  }

  public List<Problem> toList() {
    return ImmutableList.copyOf(list);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("Reported problems:\n");
    for (Problem problem : list) {
      builder.append(problem.toString());
      builder.append("\n");
    }
    return builder.toString();
  }
}
