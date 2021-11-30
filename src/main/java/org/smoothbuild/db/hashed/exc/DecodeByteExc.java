package org.smoothbuild.db.hashed.exc;

import org.smoothbuild.db.hashed.Hash;

public class DecodeByteExc extends HashedDbExc {
  public DecodeByteExc(Hash hash) {
    super("Value at " + hash + " cannot be decoded as byte.");
  }
}

