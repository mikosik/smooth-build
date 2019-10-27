package org.smoothbuild.db.values;

import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;

public class ValuesDbException extends RuntimeException {
  public static ValuesDbException corruptedValueException(Hash hash, String message) {
    return new ValuesDbException(hash.toString() + " value in ValuesDb is corrupted. " + message);
  }

  public static ValuesDbException valuesDbException(IOException e) {
    return new ValuesDbException("IOException when accessing ValuesDb", e);
  }

  public ValuesDbException(String message, Throwable e) {
    super(message, e);
  }

  public ValuesDbException(String message) {
    super(message);
  }
}
