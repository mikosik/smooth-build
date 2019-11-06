package org.smoothbuild.db.hashed;

public class NoSuchDataException extends HashedDbException {
  public NoSuchDataException(Hash hash) {
    super("No data at " + hash + ".");
  }
}
