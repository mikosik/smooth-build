package org.smoothbuild.lang.object.db;

import org.smoothbuild.db.hashed.Hash;

public class ObjectDbException extends RuntimeException {

  public ObjectDbException(Hash hash) {
    this(hash, null, null);
  }

  public ObjectDbException(Hash hash, Exception cause) {
    this(hash, null, cause);
  }

  public ObjectDbException(Hash hash, String message) {
    this(hash, message, null);
  }

  public ObjectDbException(Hash hash, String message, Throwable cause) {
    super(buildMessage(hash, message), cause);
  }

  private static String buildMessage(Hash hash, String message) {
    return "Cannot read object at " + hash + " ." + (message == null ? "" : " " + message);
  }

  public ObjectDbException(Throwable cause) {
    super(cause);
  }
}
