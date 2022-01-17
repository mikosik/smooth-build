package org.smoothbuild.bytecode.type.base;

import org.smoothbuild.db.Hash;

public abstract class ExprCatB extends CatB {
  private final TypeB evalT;

  protected ExprCatB(Hash hash, String name, CatKindB kind, TypeB evalT) {
    super(hash, name + ":" + evalT.name(), kind);
    this.evalT = evalT;
  }

  public TypeB evalT() {
    return evalT;
  }
}
