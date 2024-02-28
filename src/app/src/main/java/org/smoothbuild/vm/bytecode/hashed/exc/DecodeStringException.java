package org.smoothbuild.vm.bytecode.hashed.exc;

import org.smoothbuild.common.Hash;

public class DecodeStringException extends HashedDbException {
  public DecodeStringException(Hash hash, Throwable cause) {
    super("Value at " + hash + " cannot be decoded as string.", cause);
  }
}
