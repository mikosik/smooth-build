package org.smoothbuild.vm.bytecode.expr.exc;

import org.smoothbuild.common.Hash;

public class DecodeExprNoSuchExprException extends DecodeExprException {
  public DecodeExprNoSuchExprException(Hash hash) {
    this(hash, null);
  }

  public DecodeExprNoSuchExprException(Hash hash, Throwable cause) {
    super("Cannot decode expression at " + hash + ". Cannot find it in expression db.", cause);
  }
}
