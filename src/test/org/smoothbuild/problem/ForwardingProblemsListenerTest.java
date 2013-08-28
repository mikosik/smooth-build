package org.smoothbuild.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

public class ForwardingProblemsListenerTest {

  @Test
  public void problemsAreForwarded() {
    ProblemsListener wrapped = mock(ProblemsListener.class);
    Problem problem = new Problem(ProblemType.ERROR, null, "message");

    MyForwardingProblemsListener listener = new MyForwardingProblemsListener(wrapped);
    listener.report(problem);

    verify(wrapped).report(problem);
    assertThat(listener.getOnForwardProblem()).isSameAs(problem);
  }

  public static class MyForwardingProblemsListener extends ForwardingProblemsListener {
    private Problem problem;

    public MyForwardingProblemsListener(ProblemsListener wrapped) {
      super(wrapped);
    }

    @Override
    protected void onForward(Problem problem) {
      this.problem = problem;
    }

    public Problem getOnForwardProblem() {
      return problem;
    }
  }
}
