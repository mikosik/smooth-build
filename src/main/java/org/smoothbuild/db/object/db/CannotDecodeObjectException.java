package org.smoothbuild.db.object.db;

import org.smoothbuild.db.hashed.Hash;

public class CannotDecodeObjectException extends ObjectDbException {
  public CannotDecodeObjectException(Hash hash) {
    this(hash, null, null);
  }

  public CannotDecodeObjectException(Hash hash, Exception cause) {
    this(hash, null, cause);
  }

  public CannotDecodeObjectException(Hash hash, String message) {
    this(hash, message, null);
  }

  public CannotDecodeObjectException(Hash hash, String message, Throwable cause) {
    super(buildMessage(hash, message), cause);
  }

  private static String buildMessage(Hash hash, String message) {
    return "Cannot read object at " + hash + "." + (message == null ? "" : " " + message);
  }

  public CannotDecodeObjectException(Throwable cause) {
    super(cause);
  }
}
