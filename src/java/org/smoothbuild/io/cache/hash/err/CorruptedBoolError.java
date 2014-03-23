package org.smoothbuild.io.cache.hash.err;

@SuppressWarnings("serial")
public class CorruptedBoolError extends HashedDbError {
  public CorruptedBoolError(byte actualValue) {
    super("Expected bool value but got " + actualValue);
  }
}
