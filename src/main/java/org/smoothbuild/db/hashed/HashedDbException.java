package org.smoothbuild.db.hashed;

public class HashedDbException extends Exception {
  public HashedDbException(String message) {
    super(message);
  }

  public HashedDbException(String message, Throwable cause) {
    super(message, cause);
  }

  public HashedDbException(Hash hash, Throwable cause) {
    super("Cannot read data at " + hash + ".", cause);
  }

  public HashedDbException(Throwable cause) {
    super(cause);
  }
}
