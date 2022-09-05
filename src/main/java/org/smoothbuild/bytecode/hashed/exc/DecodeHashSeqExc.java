package org.smoothbuild.bytecode.hashed.exc;

import org.smoothbuild.bytecode.hashed.Hash;

public class DecodeHashSeqExc extends HashedDbExc {
  public DecodeHashSeqExc(Hash hash, int remainder) {
    super("Value at " + hash + " cannot be decoded as hash sequence. Its byte size should be "
        + "multiple of hash-byte-length=" + Hash.lengthInBytes() + " but it is N*hash-byte-length+"
        + remainder +".");
  }
}
