package org.smoothbuild.db.object.db;

public class ObjDbException extends RuntimeException {
  public ObjDbException(String message, Throwable cause) {
    super(message, cause);
  }

  public ObjDbException(Throwable cause) {
    super(cause);
  }

  public ObjDbException(String message) {
    super(message);
  }
}
