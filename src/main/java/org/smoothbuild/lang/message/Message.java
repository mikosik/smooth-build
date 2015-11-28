package org.smoothbuild.lang.message;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Throwables;

public class Message extends RuntimeException {
  private final MessageType type;
  private final String message;

  protected Message(MessageType type, String message) {
    this.type = checkNotNull(type);
    this.message = checkNotNull(message);
  }

  public MessageType type() {
    return type;
  }

  public String message() {
    return message;
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof Message) {
      Message that = (Message) object;
      return this.type == that.type && this.message.equals(that.message);
    }
    return false;
  }

  @Override
  public final int hashCode() {
    return this.type.hashCode() + 17 * message.hashCode();
  }

  @Override
  public String toString() {
    return type.toString() + ": " + message + stackTrace();
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
