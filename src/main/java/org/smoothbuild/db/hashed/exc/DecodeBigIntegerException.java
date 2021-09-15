package org.smoothbuild.db.hashed.exc;

import org.smoothbuild.db.hashed.Hash;

public class DecodeBigIntegerException extends HashedDbException {
  public DecodeBigIntegerException(Hash hash) {
    super("Value at " + hash + " cannot be decoded as BigInteger.");
  }
}
