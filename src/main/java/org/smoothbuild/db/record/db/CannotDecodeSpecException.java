package org.smoothbuild.db.record.db;

import org.smoothbuild.db.hashed.Hash;

public class CannotDecodeSpecException extends RecordDbException {
  public CannotDecodeSpecException(Hash hash) {
    this(hash, null, null);
  }

  public CannotDecodeSpecException(Hash hash, Exception cause) {
    this(hash, null, cause);
  }

  public CannotDecodeSpecException(Hash hash, String message) {
    this(hash, message, null);
  }

  public CannotDecodeSpecException(Hash hash, String message, Throwable cause) {
    super(buildMessage(hash, message), cause);
  }

  private static String buildMessage(Hash hash, String message) {
    return "Cannot read spec at " + hash + "." + (message == null ? "" : " " + message);
  }

  public CannotDecodeSpecException(Throwable cause) {
    super(cause);
  }
}
