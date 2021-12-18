package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ByteDbExc;

public class DecodeCatExc extends ByteDbExc {
  public DecodeCatExc(Hash hash) {
    this(hash, null, null);
  }

  public DecodeCatExc(Hash hash, Exception cause) {
    this(hash, null, cause);
  }

  public DecodeCatExc(String message) {
    super(message);
  }

  public DecodeCatExc(String message, Throwable e) {
    super(message, e);
  }

  public DecodeCatExc(Hash hash, String message) {
    this(hash, message, null);
  }

  public DecodeCatExc(Hash hash, String message, Throwable cause) {
    super(buildMessage(hash, message), cause);
  }

  private static String buildMessage(Hash hash, String message) {
    return "Cannot decode category at " + hash + "." + (message == null ? "" : " " + message);
  }
}
