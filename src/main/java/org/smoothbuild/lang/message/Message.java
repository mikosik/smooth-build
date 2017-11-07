package org.smoothbuild.lang.message;

public class Message {
  private final String message;

  protected Message(String message) {
    this.message = message;
  }

  public String message() {
    return message;
  }

  public final boolean equals(Object object) {
    if (object instanceof Message) {
      Message that = (Message) object;
      return this.getClass().equals(that.getClass()) && message.equals(that.message);
    }
    return false;
  }

  public final int hashCode() {
    return getClass().hashCode() + 17 * message.hashCode();
  }

  public String toString() {
    return name() + ": " + message;
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
}
