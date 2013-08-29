package org.smoothbuild.problem;

import com.google.inject.ImplementedBy;

@ImplementedBy(PrintingProblemsListener.class)
public interface ProblemsListener {
  public void report(Problem problem);
}
