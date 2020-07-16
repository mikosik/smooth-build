package org.smoothbuild.lang.object.type;

import static org.smoothbuild.lang.object.type.TypeKind.NOTHING;

import org.smoothbuild.lang.object.base.SObject;

public abstract class AbstractType implements Type {
  private final TypeKind kind;

  public AbstractType(TypeKind kind) {
    this.kind = kind;
  }

  @Override
  public String name() {
    return kind.name();
  }

  @Override
  public TypeKind kind() {
    return kind;
  }

  @Override
  public Class<? extends SObject> jType() {
    return kind.jType();
  }

  @Override
  public boolean isArray() {
    return this instanceof ArrayType;
  }

  @Override
  public boolean isNothing() {
    return kind == NOTHING;
  }
}
