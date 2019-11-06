package org.smoothbuild.db.hashed;

public class CorruptedHashedDbException extends HashedDbException {
  public CorruptedHashedDbException(String message) {
    super(message);
  }
}
