package org.smoothbuild.lang.object.db;

import org.smoothbuild.db.hashed.Hash;

public class ObjectsDbException extends RuntimeException {

  public ObjectsDbException(Hash hash, Exception cause) {
    this(hash, null, cause);
  }

  public ObjectsDbException(Hash hash, String message) {
    this(hash, message, null);
  }

  public ObjectsDbException(Hash hash, String message, Throwable cause) {
    super(buildMessage(hash, message), cause);
  }

  private static String buildMessage(Hash hash, String message) {
    return "Cannot read object at " + hash + " ." + (message == null ? "" : " " + message);
  }

  public ObjectsDbException(Throwable cause) {
    super(cause);
  }
}
