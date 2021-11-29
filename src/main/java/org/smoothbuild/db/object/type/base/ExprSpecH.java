package org.smoothbuild.db.object.type.base;

import org.smoothbuild.db.hashed.Hash;

public abstract class ExprSpecH extends SpecH {
  private final TypeH evaluationType;

  protected ExprSpecH(String name, Hash hash, SpecKindH kind, TypeH evaluationType) {
    super(name + ":" + evaluationType.name(), hash, kind);
    this.evaluationType = evaluationType;
  }

  public TypeH evaluationType() {
    return evaluationType;
  }
}
