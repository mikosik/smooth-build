package org.smoothbuild.problem;

public abstract class ForwardingProblemsListener implements ProblemsListener {
  private final ProblemsListener wrapped;

  public ForwardingProblemsListener(ProblemsListener wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public void report(Problem problem) {
    wrapped.report(problem);
    onForward(problem);
  }

  protected abstract void onForward(Problem problem);
}
