package org.smoothbuild.db.exc;

import java.io.IOException;

import org.smoothbuild.db.Hash;

public class HashedDbExc extends Exception {
  public HashedDbExc(String message) {
    super(message);
  }

  public HashedDbExc(String message, Throwable cause) {
    super(message, cause);
  }

  public HashedDbExc(Hash hash, IOException cause) {
    super("Cannot read data at " + hash + ".", cause);
  }

  public HashedDbExc(Throwable cause) {
    super(cause);
  }
}
