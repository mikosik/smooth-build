package org.smoothbuild.lang.base.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.Lists.list;

/**
 * This class is immutable.
 */
public class ArrayType extends Type {
  private final Type elemType;

  public ArrayType(Type elemType) {
    super("[" +  elemType.name() + "]", createTypeConstructor(), list(elemType), list(),
        elemType.isPolytype());
    this.elemType = requireNonNull(elemType);
  }

  private static TypeConstructor createTypeConstructor() {
    return new TypeConstructor("[]", 1, 0,
        (covariants, contravariants) -> new ArrayType(covariants.get(0)));
  }

  public Type elemType() {
    return elemType;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof ArrayType thatArray
        && this.elemType().equals(thatArray.elemType());
  }
}
