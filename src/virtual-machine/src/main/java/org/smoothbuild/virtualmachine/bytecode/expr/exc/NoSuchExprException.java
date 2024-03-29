package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;

public class NoSuchExprException extends DecodeExprException {
  public NoSuchExprException(Hash hash) {
    this(hash, null);
  }

  public NoSuchExprException(Hash hash, Throwable cause) {
    super("Cannot decode expression at " + hash + ". Cannot find it in expression db.", cause);
  }
}
