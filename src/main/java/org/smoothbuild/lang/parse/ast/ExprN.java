package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Loc;

public sealed class ExprN extends Node
    permits AnnN, ArrayN, BlobN, CallN, IntN, RefN, SelectN, StringN {
  public ExprN(Loc loc) {
    super(loc);
  }
}
