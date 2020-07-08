package org.smoothbuild.lang.base.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.Location.internal;

import org.smoothbuild.lang.base.type.compound.ArrayCompoundability;

public class ConcreteArrayType extends ConcreteType implements ArrayType {
  private final ConcreteType elemType;

  public ConcreteArrayType(ConcreteType elemType) {
    super("[" +  elemType.name() + "]", internal(), calculateSuperType(elemType),
        new ArrayCompoundability());
    this.elemType = requireNonNull(elemType);
  }

  @Override
  public ConcreteType elemType() {
    return elemType;
  }

  private static ConcreteType calculateSuperType(ConcreteType elemType) {
    ConcreteType elemSuperType = elemType.superType();
    if (elemSuperType == null) {
      return null;
    } else {
      return new ConcreteArrayType(elemSuperType);
    }
  }
}
