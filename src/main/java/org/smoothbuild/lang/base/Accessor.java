package org.smoothbuild.lang.base;

import org.smoothbuild.lang.base.type.ConcreteType;

public class Accessor extends Evaluable {
  private final int fieldIndex;

  public Accessor(ConcreteType type, String name, int fieldIndex, Location location) {
    super(type, name, location);
    this.fieldIndex = fieldIndex;
  }

  @Override
  public ConcreteType type() {
    return (ConcreteType) super.type();
  }

  @Override
  public String extendedName() {
    return "." + name();
  }

  public int fieldIndex() {
    return fieldIndex;
  }
}
