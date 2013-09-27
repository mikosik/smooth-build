package org.smoothbuild.message.listen;

import org.smoothbuild.message.message.Message;

public abstract class ForwardingMessageListener implements MessageListener {
  private final MessageListener wrapped;

  public ForwardingMessageListener(MessageListener wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public void report(Message message) {
    wrapped.report(message);
    onForward(message);
  }

  protected abstract void onForward(Message message);
}
