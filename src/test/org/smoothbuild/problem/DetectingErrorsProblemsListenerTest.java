package org.smoothbuild.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

public class DetectingErrorsProblemsListenerTest {
  ProblemsListener wrapped = mock(ProblemsListener.class);
  DetectingErrorsProblemsListener listener = new DetectingErrorsProblemsListener(wrapped);

  @Test
  public void problemsAreForwarded() {
    Problem problem = new Problem(ProblemType.ERROR, "message");

    listener.report(problem);

    verify(wrapped).report(problem);
  }

  @Test
  public void initiallyNothingIsDetected() {
    assertThat(listener.errorDetected()).isFalse();
  }

  @Test
  public void errorIsDetectedAfterAddingError() throws Exception {
    listener.report(new Error("message"));
    assertThat(listener.errorDetected()).isTrue();
  }

  @Test
  public void errorIsNotDetectedAfterAddingWarning() throws Exception {
    listener.report(new Warning("message"));
    assertThat(listener.errorDetected()).isFalse();
  }
}
