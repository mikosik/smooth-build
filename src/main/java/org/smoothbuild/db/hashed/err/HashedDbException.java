package org.smoothbuild.db.hashed.err;

public class HashedDbException extends RuntimeException {
  public HashedDbException(String message) {
    super("Internal error in smooth hashed DB:\n" + message);
  }
}
