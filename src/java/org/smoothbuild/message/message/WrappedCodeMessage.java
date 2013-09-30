package org.smoothbuild.message.message;

public class WrappedCodeMessage extends CodeMessage {
  private final Message wrappedMessage;

  public WrappedCodeMessage(Message message, CodeLocation codeLocation) {
    super(message.type(), codeLocation, message.message());
    this.wrappedMessage = message;
  }

  public Message wrappedMessage() {
    return wrappedMessage;
  }
}
