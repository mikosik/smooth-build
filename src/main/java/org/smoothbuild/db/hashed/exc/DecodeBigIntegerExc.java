package org.smoothbuild.db.hashed.exc;

import org.smoothbuild.db.hashed.Hash;

public class DecodeBigIntegerExc extends HashedDbExc {
  public DecodeBigIntegerExc(Hash hash) {
    super("Value at " + hash + " cannot be decoded as BigInteger.");
  }
}
