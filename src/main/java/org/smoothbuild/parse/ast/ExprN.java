package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.define.Loc;

public sealed abstract class ExprN extends Node
    permits OrderN, BlobN, CallN, IntN, RefN, SelectN, StringN {
  public ExprN(Loc loc) {
    super(loc);
  }
}
