package org.smoothbuild.problem;

public class DetectingErrorsProblemsListener extends ForwardingProblemsListener {
  private boolean detected = false;

  public DetectingErrorsProblemsListener(ProblemsListener wrapped) {
    super(wrapped);
  }

  @Override
  protected void onForward(Problem problem) {
    if (problem.type() == ProblemType.ERROR) {
      detected = true;
    }
  }

  public boolean errorDetected() {
    return detected;
  }
}
