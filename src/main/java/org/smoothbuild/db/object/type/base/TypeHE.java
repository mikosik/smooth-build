package org.smoothbuild.db.object.type.base;

import org.smoothbuild.db.hashed.Hash;

public abstract class TypeHE extends TypeH {
  private final TypeHV evaluationType;

  protected TypeHE(String name, Hash hash, TypeKindH kind, TypeHV evaluationType) {
    super(name + ":" + evaluationType.name(), hash, kind);
    this.evaluationType = evaluationType;
  }

  public TypeHV evaluationType() {
    return evaluationType;
  }
}
