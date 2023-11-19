package org.smoothbuild.vm.bytecode.hashed.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;

public class DecodeHashSeqException extends HashedDbException {
  public DecodeHashSeqException(Hash hash, long remainder) {
    super("Value at " + hash + " cannot be decoded as hash sequence. Its byte size should be "
        + "multiple of hash-byte-length=" + Hash.lengthInBytes() + " but it is N*hash-byte-length+"
        + remainder + ".");
  }
}
