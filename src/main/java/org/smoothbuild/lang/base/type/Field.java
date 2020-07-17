package org.smoothbuild.lang.base.type;

import org.smoothbuild.lang.base.Item;
import org.smoothbuild.lang.base.Location;

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
