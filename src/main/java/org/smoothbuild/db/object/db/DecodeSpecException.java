package org.smoothbuild.db.object.db;

import org.smoothbuild.db.hashed.Hash;

public class DecodeSpecException extends ObjectDbException {
  public DecodeSpecException(Hash hash) {
    this(hash, null, null);
  }

  public DecodeSpecException(Hash hash, Exception cause) {
    this(hash, null, cause);
  }

  public DecodeSpecException(Hash hash, String message) {
    this(hash, message, null);
  }

  public DecodeSpecException(Hash hash, String message, Throwable cause) {
    super(buildMessage(hash, message), cause);
  }

  private static String buildMessage(Hash hash, String message) {
    return "Cannot read spec at " + hash + "." + (message == null ? "" : " " + message);
  }

  public DecodeSpecException(Throwable cause) {
    super(cause);
  }
}
