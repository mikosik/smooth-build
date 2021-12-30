package org.smoothbuild.db.exc;

import org.smoothbuild.db.Hash;

public class DecodeByteExc extends HashedDbExc {
  public DecodeByteExc(Hash hash) {
    super("Value at " + hash + " cannot be decoded as byte.");
  }
}

