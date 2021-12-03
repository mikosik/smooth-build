package org.smoothbuild.db.object.type.base;

import org.smoothbuild.db.hashed.Hash;

public abstract class ExprCatH extends CatH {
  private final TypeH evalType;

  protected ExprCatH(String name, Hash hash, CatKindH kind, TypeH evalType) {
    super(name + ":" + evalType.name(), hash, kind);
    this.evalType = evalType;
  }

  public TypeH evalType() {
    return evalType;
  }
}
