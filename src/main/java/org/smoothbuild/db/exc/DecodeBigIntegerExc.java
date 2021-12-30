package org.smoothbuild.db.exc;

import org.smoothbuild.db.Hash;

public class DecodeBigIntegerExc extends HashedDbExc {
  public DecodeBigIntegerExc(Hash hash) {
    super("Value at " + hash + " cannot be decoded as BigInteger.");
  }
}
