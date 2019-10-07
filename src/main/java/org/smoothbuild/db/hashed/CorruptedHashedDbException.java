package org.smoothbuild.db.hashed;

import java.io.IOException;

public class CorruptedHashedDbException extends IOException {
  public CorruptedHashedDbException(String message) {
    super(message);
  }
}
