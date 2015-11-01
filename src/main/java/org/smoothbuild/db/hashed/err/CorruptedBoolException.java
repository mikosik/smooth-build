package org.smoothbuild.db.hashed.err;

public class CorruptedBoolException extends HashedDbException {
  public CorruptedBoolException(byte actualValue) {
    super("Expected bool value but got " + actualValue);
  }
}
