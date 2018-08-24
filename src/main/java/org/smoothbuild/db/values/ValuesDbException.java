package org.smoothbuild.db.values;

import com.google.common.hash.HashCode;

public class ValuesDbException extends RuntimeException {
  public static ValuesDbException corruptedHashSequenceException(HashCode hash) {
    return corruptedValueException(hash,
        "Expected sequence of hashes but number of bytes is not multiple of hash size.");
  }

  public static ValuesDbException corruptedValueException(HashCode hash, String message) {
    return new ValuesDbException(hash.toString() + " value in ValuesDb is corrupted. " + message);
  }

  public static ValuesDbException readException(Throwable e) {
    return new ValuesDbException("IOException when reading from ValuesDb", e);
  }

  public ValuesDbException(String message, Throwable e) {
    super(message, e);
  }

  public ValuesDbException(String message) {
    super(message);
  }
}
