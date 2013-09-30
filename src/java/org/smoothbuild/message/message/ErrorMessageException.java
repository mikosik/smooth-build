package org.smoothbuild.message.message;

@SuppressWarnings("serial")
public class ErrorMessageException extends RuntimeException {
  private final ErrorMessage errorMessage;

  public ErrorMessageException(ErrorMessage errorMessage) {
    super(errorMessage.message());
    this.errorMessage = errorMessage;
  }

  public ErrorMessage errorMessage() {
    return errorMessage;
  }
}
