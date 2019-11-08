package org.smoothbuild.lang.object.db;

import org.smoothbuild.db.hashed.Hash;

public class DecodingStringException extends ValuesDbException {
  public DecodingStringException(Hash hash, Throwable cause) {
    super("Value at " + hash + " cannot be decoded as string.", cause);
  }
}
