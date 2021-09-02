package org.smoothbuild.db.hashed;

public class DecodeHashSequenceException extends HashedDbException {
  public DecodeHashSequenceException(Hash hash, int remainder) {
    super("Value at " + hash + " cannot be decoded as hash sequence. Its byte size should be "
        + "multiple of hash-byte-length=" + Hash.hashesSize() + " but it is N*hash-byte-length+"
        + remainder +".");
  }
}
