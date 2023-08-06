package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.Hash;

public class DecodeExprCatException extends DecodeExprException {
  public DecodeExprCatException(Hash hash) {
    this(hash, null);
  }

  public DecodeExprCatException(Hash hash, Throwable e) {
    super("Cannot decode object at " + hash + ". Cannot decode its category.", e);
  }
}
