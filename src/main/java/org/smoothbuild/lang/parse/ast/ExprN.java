package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Loc;

public sealed abstract class ExprN extends Node
    permits OrderN, BlobN, CallN, IntN, RefN, SelectN, StringN {
  public ExprN(Loc loc) {
    super(loc);
  }
}
