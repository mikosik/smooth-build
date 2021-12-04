package org.smoothbuild.db.object.type.base;

import org.smoothbuild.db.hashed.Hash;

public abstract class ExprCatH extends CatH {
  private final TypeH evalT;

  protected ExprCatH(String name, Hash hash, CatKindH kind, TypeH evalT) {
    super(name + ":" + evalT.name(), hash, kind);
    this.evalT = evalT;
  }

  public TypeH evalT() {
    return evalT;
  }
}
