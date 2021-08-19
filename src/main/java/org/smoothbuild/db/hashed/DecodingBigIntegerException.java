package org.smoothbuild.db.hashed;

public class DecodingBigIntegerException extends HashedDbException {
  public DecodingBigIntegerException(Hash hash) {
    super("Value at " + hash + " cannot be decoded as BigInteger.");
  }
}
