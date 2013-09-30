package org.smoothbuild.message.message;

@SuppressWarnings("serial")
public class ErrorMessageException extends RuntimeException {
  private final Error error;

  public ErrorMessageException(Error error) {
    super(error.message());
    this.error = error;
  }

  public Error error() {
    return error;
  }
}
