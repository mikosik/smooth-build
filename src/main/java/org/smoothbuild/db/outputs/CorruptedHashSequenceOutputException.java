package org.smoothbuild.db.outputs;

import com.google.common.hash.HashCode;

public class CorruptedHashSequenceOutputException extends CorruptedOutputException {
  public CorruptedHashSequenceOutputException(HashCode hash) {
    super(hash, "Expected sequence of hashes but number of bytes is not multiple of hash size.");
  }
}
