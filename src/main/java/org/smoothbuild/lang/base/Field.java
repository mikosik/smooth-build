package org.smoothbuild.lang.base;

import org.smoothbuild.lang.base.type.ConcreteType;

public class Field extends Item {
  public Field(int index, ConcreteType type, String name, Location location) {
    super(index, type, name, false, location);
  }

  @Override
  public ConcreteType type() {
    return (ConcreteType) super.type();
  }

  @Override
  public String toString() {
    return "Field{" +
        "type=" + type() +
        ", name='" + name() +
        '}';
  }
}
