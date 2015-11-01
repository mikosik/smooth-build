package org.smoothbuild.db.hashed.err;

public class HashedDbError extends RuntimeException {
  public HashedDbError(String message) {
    super("Internal error in smooth hashed DB:\n" + message);
  }
}
