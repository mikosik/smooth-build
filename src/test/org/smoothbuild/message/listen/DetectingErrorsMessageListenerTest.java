package org.smoothbuild.message.listen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.message.message.MessageType.WARNING;

import org.junit.Test;
import org.smoothbuild.message.message.Message;

public class DetectingErrorsMessageListenerTest {
  MessageListener wrapped = mock(MessageListener.class);
  DetectingErrorsMessageListener listener = new DetectingErrorsMessageListener(wrapped);

  @Test
  public void messagesAreForwarded() {
    Message message = new Message(ERROR, "message");

    listener.report(message);

    verify(wrapped).report(message);
  }

  @Test
  public void initiallyNothingIsDetected() {
    assertThat(listener.errorDetected()).isFalse();
  }

  @Test
  public void errorIsDetectedAfterAddingError() throws Exception {
    listener.report(new Message(ERROR, "message"));
    assertThat(listener.errorDetected()).isTrue();
  }

  @Test
  public void errorIsNotDetectedAfterAddingWarning() throws Exception {
    listener.report(new Message(WARNING, "message"));
    assertThat(listener.errorDetected()).isFalse();
  }
}
