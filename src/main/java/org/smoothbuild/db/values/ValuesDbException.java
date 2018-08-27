package org.smoothbuild.db.values;

import java.io.IOException;

import com.google.common.hash.HashCode;

public class ValuesDbException extends RuntimeException {
  public static ValuesDbException newCorruptedMerkleRootException(HashCode hash, int childCount) {
    return corruptedValueException(
        hash, "Its Merkle tree root has " + childCount + " children.");
  }

  public static ValuesDbException corruptedValueException(HashCode hash, String message) {
    return new ValuesDbException(hash.toString() + " value in ValuesDb is corrupted. " + message);
  }

  public static ValuesDbException ioException(IOException e) {
    return new ValuesDbException("IOException when accessing ValuesDb", e);
  }

  public ValuesDbException(String message, Throwable e) {
    super(message, e);
  }

  public ValuesDbException(String message) {
    super(message);
  }
}
