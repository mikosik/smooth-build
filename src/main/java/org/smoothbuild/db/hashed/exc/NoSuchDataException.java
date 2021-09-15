package org.smoothbuild.db.hashed.exc;

import org.smoothbuild.db.hashed.Hash;

public class NoSuchDataException extends HashedDbException {
  public NoSuchDataException(Hash hash) {
    super("No data at " + hash + ".");
  }
}
