package org.smoothbuild.virtualmachine.bytecode.hashed.exc;

import org.smoothbuild.common.base.Hash;

public class DecodeStringException extends HashedDbException {
  public DecodeStringException(Hash hash, Throwable cause) {
    super("Value at " + hash + " cannot be decoded as string.", cause);
  }
}
