package org.smoothbuild.db.hashed;

import java.io.IOException;

public class HashedDbException extends Exception {
  public HashedDbException(String message) {
    super(message);
  }

  public HashedDbException(Hash hash, IOException e) {
    super("Cannot read data at " + hash + ".", e);
  }

  public HashedDbException(IOException e) {
    super(e);
  }
}
