package org.smoothbuild.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

public class DetectingErrorsMessageListenerTest {
  MessageListener wrapped = mock(MessageListener.class);
  DetectingErrorsMessageListener listener = new DetectingErrorsMessageListener(wrapped);

  @Test
  public void messagesAreForwarded() {
    Message message = new Message(MessageType.ERROR, "message");

    listener.report(message);

    verify(wrapped).report(message);
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
