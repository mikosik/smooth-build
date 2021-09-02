package org.smoothbuild.db.object.db;

import org.smoothbuild.db.hashed.Hash;

public class DecodeObjException extends ObjectDbException {
  public DecodeObjException(Hash hash) {
    this(hash, null, null);
  }

  public DecodeObjException(Hash hash, Exception cause) {
    this(hash, null, cause);
  }

  public DecodeObjException(Hash hash, String message) {
    this(hash, message, null);
  }

  public DecodeObjException(Hash hash, String message, Throwable cause) {
    super(buildMessage(hash, message), cause);
  }

  private static String buildMessage(Hash hash, String message) {
    return "Cannot decode object at " + hash + "." + (message == null ? "" : " " + message);
  }
}
