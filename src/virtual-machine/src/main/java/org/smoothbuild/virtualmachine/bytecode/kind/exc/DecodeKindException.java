package org.smoothbuild.virtualmachine.bytecode.kind.exc;

import org.smoothbuild.common.base.Hash;

public class DecodeKindException extends BKindDbException {
  public DecodeKindException(Hash hash) {
    super(message(hash));
  }

  public DecodeKindException(Hash hash, Exception cause) {
    super(message(hash), cause);
  }

  public DecodeKindException(String message) {
    super(message);
  }

  public DecodeKindException(String message, Throwable cause) {
    super(message, cause);
  }

  private static String message(Hash hash) {
    return "Cannot decode kind at " + hash + ".";
  }
}
