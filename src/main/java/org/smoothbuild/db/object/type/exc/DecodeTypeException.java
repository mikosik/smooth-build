package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjDbException;

public class DecodeTypeException extends ObjDbException {
  public DecodeTypeException(Hash hash) {
    this(hash, null, null);
  }

  public DecodeTypeException(Hash hash, Exception cause) {
    this(hash, null, cause);
  }

  public DecodeTypeException(String message) {
    super(message);
  }

  public DecodeTypeException(String message, Throwable e) {
    super(message, e);
  }

  public DecodeTypeException(Hash hash, String message) {
    this(hash, message, null);
  }

  public DecodeTypeException(Hash hash, String message, Throwable cause) {
    super(buildMessage(hash, message), cause);
  }

  private static String buildMessage(Hash hash, String message) {
    return "Cannot decode type at " + hash + "." + (message == null ? "" : " " + message);
  }
}
