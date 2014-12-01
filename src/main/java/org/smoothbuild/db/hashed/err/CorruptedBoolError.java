package org.smoothbuild.db.hashed.err;

public class CorruptedBoolError extends HashedDbError {
  public CorruptedBoolError(byte actualValue) {
    super("Expected bool value but got " + actualValue);
  }
}
