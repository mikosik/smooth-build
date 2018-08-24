package org.smoothbuild.db.values;

import java.io.IOException;

import com.google.common.hash.HashCode;

public class ValuesDbException extends RuntimeException {
  public static ValuesDbException newCorruptedMerkleRootException(HashCode hash, int childCount) {
    return corruptedValueException(
        hash, "Its Merkle tree root has " + childCount + " children.");
  }

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

  public static ValuesDbException writeException(IOException e) {
    return new ValuesDbException("IOException when writing to ValuesDb", e);
  }

  public ValuesDbException(String message, Throwable e) {
    super(message, e);
  }

  public ValuesDbException(String message) {
    super(message);
  }
}
