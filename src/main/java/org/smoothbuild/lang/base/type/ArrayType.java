package org.smoothbuild.lang.base.type;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class ArrayType extends Type {
  private final Type elemType;

  public ArrayType(Type elemType) {
    super("[" +  elemType.name() + "]", createTypeConstructor(),
        ImmutableList.of(elemType), ImmutableList.of(), elemType.isPolytype());
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
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object instanceof ArrayType thatArray) {
      return this.elemType().equals(thatArray.elemType());
    }
    return false;
  }
}
