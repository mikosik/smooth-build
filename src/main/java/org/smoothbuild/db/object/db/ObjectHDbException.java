package org.smoothbuild.db.object.db;

public class ObjectHDbException extends RuntimeException {
  public ObjectHDbException(String message, Throwable cause) {
    super(message, cause);
  }

  public ObjectHDbException(Throwable cause) {
    super(cause);
  }

  public ObjectHDbException(String message) {
    super(message);
  }
}
