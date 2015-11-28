package org.smoothbuild.lang.message;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Throwables;

public class Message extends RuntimeException {
  private final String message;

  protected Message(String message) {
    this.message = checkNotNull(message);
  }

  public String message() {
    return message;
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof Message) {
      Message that = (Message) object;
      return this.getClass().equals(that.getClass()) && this.message.equals(that.message);
    }
    return false;
  }

  @Override
  public final int hashCode() {
    return getClass().hashCode() + 17 * message.hashCode();
  }

  @Override
  public String toString() {
    return name() + ": " + message + stackTrace();
  }

  private String name() {
    if (this instanceof ErrorMessage) {
      return "ERROR";
    } else if (this instanceof WarningMessage) {
      return "WARNING";
    } else if (this instanceof InfoMessage) {
      return "INFO";
    } else {
      throw new RuntimeException("Unknown message type: " + getClass().getCanonicalName());
    }
  }

  private String stackTrace() {
    Throwable cause = getCause();
    if (cause == null) {
      return "";
    } else {
      return "\n" + Throwables.getStackTraceAsString(cause);
    }
  }
}
