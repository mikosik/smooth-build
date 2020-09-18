package org.smoothbuild.lang.base;

import org.smoothbuild.lang.base.type.Type;

/**
 * This class is immutable.
 */
public class Field extends Item {
  public Field(int index, Type type, String name, Location location) {
    super(index, type, name, false, location);
  }

  @Override
  public Type type() {
    return super.type();
  }

  @Override
  public String toString() {
    return "Field(`" + type() + ", " + name() + "`)";
  }
}
