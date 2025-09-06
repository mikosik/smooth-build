package org.smoothbuild.virtualmachine.bytecode.hashed.exc;

import org.smoothbuild.common.base.Hash;

public class DecodeStringException extends HashedDbException {
  public DecodeStringException(Hash hash) {
    super(message(hash));
  }

  public DecodeStringException(Hash hash, Throwable cause) {
    super(message(hash), cause);
  }

  private static String message(Hash hash) {
    return "Value at " + hash + " cannot be decoded as string.";
  }
}
