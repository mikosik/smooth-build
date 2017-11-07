package org.smoothbuild.lang.message;

public class MessageException extends RuntimeException {
  private final Message message;

  public static MessageException errorException(String message) {
    return new MessageException(new ErrorMessage(message));
  }

  public static MessageException warningException(String message) {
    return new MessageException(new WarningMessage(message));
  }

  public static MessageException infoException(String message) {
    return new MessageException(new InfoMessage(message));
  }

  public MessageException(Message message) {
    this.message = message;
  }

  public Message message() {
    return message;
  }
}
