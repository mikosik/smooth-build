package org.smoothbuild.db.object.spec.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDbException;

public class DecodeSpecException extends ObjectDbException {
  public DecodeSpecException(Hash hash) {
    this(hash, null, null);
  }

  public DecodeSpecException(Hash hash, Exception cause) {
    this(hash, null, cause);
  }

  public DecodeSpecException(String message) {
    super(message);
  }

  public DecodeSpecException(String message, Throwable e) {
    super(message, e);
  }

  public DecodeSpecException(Hash hash, String message) {
    this(hash, message, null);
  }

  public DecodeSpecException(Hash hash, String message, Throwable cause) {
    super(buildMessage(hash, message), cause);
  }

  private static String buildMessage(Hash hash, String message) {
    return "Cannot decode spec at " + hash + "." + (message == null ? "" : " " + message);
  }
}
