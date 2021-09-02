package org.smoothbuild.db.hashed;

public class DecodeBigIntegerException extends HashedDbException {
  public DecodeBigIntegerException(Hash hash) {
    super("Value at " + hash + " cannot be decoded as BigInteger.");
  }
}
