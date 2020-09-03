package org.smoothbuild.lang.base;

import org.smoothbuild.lang.base.type.ConcreteType;
import org.smoothbuild.lang.base.type.Field;

public class FieldRead extends Evaluable {
  private final Field field;

  public FieldRead(Field field, Location location) {
    super(field.type(), field.name(), location);
    this.field = field;
  }

  @Override
  public ConcreteType type() {
    return field.type();
  }

  @Override
  public String extendedName() {
    return "." + name();
  }

  public int fieldIndex() {
    return field.index();
  }
}
