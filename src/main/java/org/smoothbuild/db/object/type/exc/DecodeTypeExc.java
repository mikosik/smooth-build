package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjDbExc;

public class DecodeTypeExc extends ObjDbExc {
  public DecodeTypeExc(Hash hash) {
    this(hash, null, null);
  }

  public DecodeTypeExc(Hash hash, Exception cause) {
    this(hash, null, cause);
  }

  public DecodeTypeExc(String message) {
    super(message);
  }

  public DecodeTypeExc(String message, Throwable e) {
    super(message, e);
  }

  public DecodeTypeExc(Hash hash, String message) {
    this(hash, message, null);
  }

  public DecodeTypeExc(Hash hash, String message, Throwable cause) {
    super(buildMessage(hash, message), cause);
  }

  private static String buildMessage(Hash hash, String message) {
    return "Cannot decode type at " + hash + "." + (message == null ? "" : " " + message);
  }
}
