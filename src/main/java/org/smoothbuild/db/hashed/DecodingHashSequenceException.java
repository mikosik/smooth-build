package org.smoothbuild.db.hashed;

public class DecodingHashSequenceException extends HashedDbException {
  public DecodingHashSequenceException(Hash hash, int remainder) {
    super("Value at " + hash + " cannot be decoded as hash sequence. Its byte size should be "
        + "multiple of hash-byte-length=" + Hash.hashesSize() + " but it is N*hash-byte-length+"
        + remainder +".");
  }

  public DecodingHashSequenceException(Hash hash, int expectedSize, int actualSize) {
    super("Hash sequence at " + hash + " has unexpected size. Expected " + expectedSize + ", " +
        "actual " + actualSize + ".");
  }

  public DecodingHashSequenceException(Hash hash, int minExpectedSize, int maxExpectedSize,
      int actualSize) {
    super("Hash sequence at " + hash + " has unexpected size. Expected range " + minExpectedSize +
        ".." + maxExpectedSize + " actual " + actualSize + ".");
  }
}
