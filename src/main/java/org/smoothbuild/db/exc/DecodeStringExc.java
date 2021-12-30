package org.smoothbuild.db.exc;

import org.smoothbuild.db.Hash;

public class DecodeStringExc extends HashedDbExc {
  public DecodeStringExc(Hash hash, Throwable cause) {
    super("Value at " + hash + " cannot be decoded as string.", cause);
  }
}
