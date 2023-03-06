package org.smoothbuild.vm.bytecode.expr.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;

public class DecodeExprNoSuchExprExc extends DecodeExprExc {
  public DecodeExprNoSuchExprExc(Hash hash) {
    this(hash, null);
  }

  public DecodeExprNoSuchExprExc(Hash hash, Throwable cause) {
    super("Cannot decode object at " + hash + ". Cannot find it in object db.", cause);
  }
}
