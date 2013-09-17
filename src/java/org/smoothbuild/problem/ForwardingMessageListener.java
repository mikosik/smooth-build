package org.smoothbuild.problem;

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
