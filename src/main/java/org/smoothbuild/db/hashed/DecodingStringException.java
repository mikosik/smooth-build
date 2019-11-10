package org.smoothbuild.db.hashed;

public class DecodingStringException extends HashedDbException {
  public DecodingStringException(Hash hash, Throwable cause) {
    super("Value at " + hash + " cannot be decoded as string.", cause);
  }
}
