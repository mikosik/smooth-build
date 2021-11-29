package org.smoothbuild.db.object.type.base;

import org.smoothbuild.db.hashed.Hash;

public abstract class ExprSpecH extends SpecH {
  private final TypeH evalType;

  protected ExprSpecH(String name, Hash hash, SpecKindH kind, TypeH evalType) {
    super(name + ":" + evalType.name(), hash, kind);
    this.evalType = evalType;
  }

  public TypeH evalType() {
    return evalType;
  }
}
