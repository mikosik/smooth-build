package org.smoothbuild.bytecode.expr.exc;

import org.smoothbuild.bytecode.hashed.Hash;

public class DecodeExprNoSuchExprExc extends DecodeExprExc {
  public DecodeExprNoSuchExprExc(Hash hash) {
    this(hash, null);
  }

  public DecodeExprNoSuchExprExc(Hash hash, Throwable cause) {
    super("Cannot decode object at " + hash + ". Cannot find it in object db.", cause);
  }
}
