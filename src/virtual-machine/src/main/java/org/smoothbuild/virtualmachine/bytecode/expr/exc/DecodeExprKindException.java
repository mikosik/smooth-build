package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;

public class DecodeExprKindException extends DecodeExprException {
  public DecodeExprKindException(Hash hash) {
    this(hash, null);
  }

  public DecodeExprKindException(Hash hash, Throwable e) {
    super("Cannot decode expression at " + hash + ". Cannot decode its kind.", e);
  }
}
