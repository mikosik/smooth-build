package org.smoothbuild.db.hashed;

import java.io.IOException;

public class NoSuchDataException extends IOException {
  public NoSuchDataException(Hash hash) {
    super("No data at " + hash + ".");
  }
}
