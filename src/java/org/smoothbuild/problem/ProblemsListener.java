package org.smoothbuild.problem;

import com.google.inject.ImplementedBy;

@ImplementedBy(Problems.class)
public interface ProblemsListener {
  public void report(Problem problem);

  public boolean hasAnyProblem();
}
