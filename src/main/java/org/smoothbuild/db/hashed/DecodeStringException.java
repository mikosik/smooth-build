package org.smoothbuild.db.hashed;

public class DecodeStringException extends HashedDbException {
  public DecodeStringException(Hash hash, Throwable cause) {
    super("Value at " + hash + " cannot be decoded as string.", cause);
  }
}
