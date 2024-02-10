package org.smoothbuild.vm.bytecode.expr.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;

public class DecodeExprNoSuchExprException extends DecodeExprException {
  public DecodeExprNoSuchExprException(Hash hash) {
    this(hash, null);
  }

  public DecodeExprNoSuchExprException(Hash hash, Throwable cause) {
    super("Cannot decode object at " + hash + ". Cannot find it in bytecode db.", cause);
  }
}
