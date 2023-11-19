package org.smoothbuild.vm.bytecode.hashed.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;

public class DecodeByteException extends HashedDbException {
  public DecodeByteException(Hash hash) {
    super("Value at " + hash + " cannot be decoded as byte.");
  }
}
