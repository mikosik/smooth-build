package org.smoothbuild.bytecode.type.base;

import org.smoothbuild.db.hashed.Hash;

public abstract class ExprCatB extends CatB {
  private final TypeB evalT;

  protected ExprCatB(String name, Hash hash, CatKindB kind, TypeB evalT) {
    super(name + ":" + evalT.name(), hash, kind);
    this.evalT = evalT;
  }

  public TypeB evalT() {
    return evalT;
  }
}
