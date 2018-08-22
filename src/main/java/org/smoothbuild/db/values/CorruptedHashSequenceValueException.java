package org.smoothbuild.db.values;

import com.google.common.hash.HashCode;

public class CorruptedHashSequenceValueException extends CorruptedValueException {
  public CorruptedHashSequenceValueException(HashCode hash) {
    super(hash, "Expected sequence of hashes but number of bytes is not multiple of hash size.");
  }
}
