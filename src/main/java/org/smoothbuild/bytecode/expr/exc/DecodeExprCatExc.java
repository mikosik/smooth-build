package org.smoothbuild.bytecode.expr.exc;

import org.smoothbuild.db.Hash;

public class DecodeExprCatExc extends DecodeExprExc {
  public DecodeExprCatExc(Hash hash) {
    this(hash, null);
  }

  public DecodeExprCatExc(Hash hash, Throwable e) {
    super("Cannot decode object at " + hash + ". Cannot decode its category.", e);
  }
}
