package org.smoothbuild.vm.bytecode.expr.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;

public class DecodeExprCatException extends DecodeExprException {
  public DecodeExprCatException(Hash hash) {
    this(hash, null);
  }

  public DecodeExprCatException(Hash hash, Throwable e) {
    super("Cannot decode object at " + hash + ". Cannot decode its category.", e);
  }
}
