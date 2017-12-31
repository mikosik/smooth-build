package org.smoothbuild.db.values;

import com.google.common.hash.HashCode;

public class CorruptedValueException extends RuntimeException {
  public CorruptedValueException(HashCode hash, String message) {
    super("Value " + hash.toString() + " is corrupted. " + message);
  }

  public CorruptedValueException(String message) {
    super(message);
  }
}
