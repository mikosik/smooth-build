package org.smoothbuild.db.hashed;

public class HashedDbException extends RuntimeException {
  public HashedDbException(String message) {
    super(message);
  }

  public HashedDbException(String message, Throwable cause) {
    super(message, cause);
  }
}
