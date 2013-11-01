package org.smoothbuild.db.hash.err;

public class CorruptedBoolError extends HashedDbError {
  public CorruptedBoolError(byte actualValue) {
    super("Expected bool value but got " + actualValue);
  }
}
