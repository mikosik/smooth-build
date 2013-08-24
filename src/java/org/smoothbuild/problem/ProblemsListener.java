package org.smoothbuild.problem;

public interface ProblemsListener {
  public void report(Problem problem);

  public boolean hasAnyProblem();
}
