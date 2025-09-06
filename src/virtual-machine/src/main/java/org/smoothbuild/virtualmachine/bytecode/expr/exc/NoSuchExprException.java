package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;

public class NoSuchExprException extends DecodeExprException {
  public NoSuchExprException(Hash hash) {
    super(message(hash));
  }

  public NoSuchExprException(Hash hash, Throwable cause) {
    super(message(hash), cause);
  }

  private static String message(Hash hash) {
    return "Cannot decode expression at " + hash + ". Cannot find it in expression db.";
  }
}
