package org.smoothbuild.virtualmachine.bytecode.hashed.exc;

import org.smoothbuild.common.base.Hash;

public class DecodeHashChainException extends HashedDbException {
  public DecodeHashChainException(Hash hash, long remainder) {
    super("Value at " + hash + " cannot be decoded as hash chain. Its byte size should be "
        + "multiple of hash-byte-length=" + Hash.lengthInBytes() + " but it is N*hash-byte-length+"
        + remainder + ".");
  }
}
