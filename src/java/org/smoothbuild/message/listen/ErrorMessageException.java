package org.smoothbuild.message.listen;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.message.message.Message;

@SuppressWarnings("serial")
public class ErrorMessageException extends RuntimeException {
  private final Message errorMessage;

  public ErrorMessageException(Message errorMessage) {
    super(errorMessage.message());
    checkArgument(errorMessage.type().isProblem(),
        "Only Message with type that is a problem can be thrown.");
    this.errorMessage = errorMessage;
  }

  public Message errorMessage() {
    return errorMessage;
  }
}
