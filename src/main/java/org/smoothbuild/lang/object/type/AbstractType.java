package org.smoothbuild.lang.object.type;

import static org.smoothbuild.lang.object.type.TypeNames.NOTHING;

import org.smoothbuild.lang.object.base.SObject;

public abstract class AbstractType implements Type {
  private final String name;
  private final Class<? extends SObject> jType;

  public AbstractType(String name, Class<? extends SObject> jType) {
    this.name = name;
    this.jType = jType;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String q() {
    return "'" + name + "'";
  }

  @Override
  public Class<? extends SObject> jType() {
    return jType;
  }

  @Override
  public boolean isArray() {
    return this instanceof ArrayType;
  }

  @Override
  public boolean isNothing() {
    return name.equals(NOTHING);
  }
}
