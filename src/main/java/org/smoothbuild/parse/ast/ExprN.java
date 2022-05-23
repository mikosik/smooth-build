package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.like.Expr;
import org.smoothbuild.lang.type.TypeS;

public sealed abstract class ExprN extends Node implements Expr
    permits OrderN, BlobN, CallN, IntN, RefN, SelectN, StringN {
  public ExprN(Loc loc) {
    super(loc);
  }

  @Override
  public Optional<TypeS> typeO() {
    return type();
  }
}
