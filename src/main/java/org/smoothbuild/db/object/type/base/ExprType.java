package org.smoothbuild.db.object.type.base;

import org.smoothbuild.db.hashed.Hash;

public abstract class ExprType extends ObjType {
  private final ValType evaluationType;

  protected ExprType(String name, Hash hash, ObjKind kind, ValType evaluationType) {
    super(name + ":" + evaluationType.name(), hash, kind);
    this.evaluationType = evaluationType;
  }

  public ValType evaluationType() {
    return evaluationType;
  }
}
