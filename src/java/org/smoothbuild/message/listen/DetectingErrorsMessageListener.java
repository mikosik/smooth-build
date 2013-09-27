package org.smoothbuild.message.listen;

import org.smoothbuild.message.message.Message;

public class DetectingErrorsMessageListener extends ForwardingMessageListener {
  private boolean detected = false;

  public DetectingErrorsMessageListener(MessageListener wrapped) {
    super(wrapped);
  }

  @Override
  protected void onForward(Message message) {
    if (message.type() == MessageType.ERROR) {
      detected = true;
    }
  }

  public boolean errorDetected() {
    return detected;
  }
}
