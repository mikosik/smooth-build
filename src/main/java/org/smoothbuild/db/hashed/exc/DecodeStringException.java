package org.smoothbuild.db.hashed.exc;

import org.smoothbuild.db.hashed.Hash;

public class DecodeStringException extends HashedDbException {
  public DecodeStringException(Hash hash, Throwable cause) {
    super("Value at " + hash + " cannot be decoded as string.", cause);
  }
}
