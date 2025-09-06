package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;

public class DecodeExprKindException extends DecodeExprException {
  public DecodeExprKindException(Hash hash) {
    super(message(hash));
  }

  public DecodeExprKindException(Hash hash, Throwable e) {
    super(message(hash), e);
  }

  private static String message(Hash hash) {
    return "Cannot decode expression at " + hash + ". Cannot decode its kind.";
  }
}
