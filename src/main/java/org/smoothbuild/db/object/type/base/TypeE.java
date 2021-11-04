package org.smoothbuild.db.object.type.base;

import org.smoothbuild.db.hashed.Hash;

public abstract class TypeE extends TypeO {
  private final TypeV evaluationType;

  protected TypeE(String name, Hash hash, ObjKind kind, TypeV evaluationType) {
    super(name + ":" + evaluationType.name(), hash, kind);
    this.evaluationType = evaluationType;
  }

  public TypeV evaluationType() {
    return evaluationType;
  }
}
