package org.smoothbuild.virtualmachine.bytecode.hashed.exc;

import org.smoothbuild.common.Hash;

public class DecodeByteException extends HashedDbException {
  public DecodeByteException(Hash hash) {
    super("Value at " + hash + " cannot be decoded as byte.");
  }
}
