package org.smoothbuild.lang.object.db;

import org.smoothbuild.db.hashed.Hash;

public class ValuesDbException extends Exception {
  public ValuesDbException(String message, Throwable cause) {
    super(message, cause);
  }

  public ValuesDbException(Throwable cause) {
    super(cause);
  }

  public ValuesDbException(String message) {
    super(message);
  }

  public ValuesDbException(Hash hash, Throwable cause) {
    super("Cannot read value at " + hash + ".", cause);
  }
}
