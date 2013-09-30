package org.smoothbuild.message.listen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.message.listen.MessageType.ERROR;

import org.junit.Test;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.WarningMessage;

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
    listener.report(new WarningMessage("message"));
    assertThat(listener.errorDetected()).isFalse();
  }
}
