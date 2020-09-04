package org.smoothbuild.db.object.db;

public class ObjectDbException extends RuntimeException {
  public ObjectDbException(String message, Throwable cause) {
    super(message, cause);
  }

  public ObjectDbException(Throwable cause) {
    super(cause);
  }

  public ObjectDbException(String message) {
    super(message);
  }
}
