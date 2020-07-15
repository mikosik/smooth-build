package org.smoothbuild.db.hashed;

public class DecodingByteException extends HashedDbException {
  public DecodingByteException(Hash hash) {
    super("Value at " + hash + " cannot be decoded as byte.");
  }
}

