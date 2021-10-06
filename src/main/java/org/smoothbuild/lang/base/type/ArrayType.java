package org.smoothbuild.lang.base.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.Lists.list;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.smoothbuild.lang.base.type.Sides.Side;

/**
 * This class is immutable.
 */
public class ArrayType extends Type {
  private final Type elemType;

  public ArrayType(Type elemType) {
    super("[" +  elemType.name() + "]", createTypeConstructor(), list(elemType), list(),
        elemType.variables());
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
  public boolean contains(Type that) {
    return this.equals(that) || elemType.contains(that);
  }

  @Override
  Type mapVariables(BoundsMap boundsMap, Side side, TypeFactory typeFactory) {
    if (isPolytype()) {
      Type elemTypeMapped = elemType.mapVariables(boundsMap, side, typeFactory);
      return createArrayType(elemTypeMapped, typeFactory);
    } else {
      return this;
    }
  }

  @Override
  Type strip(TypeFactory typeFactory) {
    return createArrayType(elemType.strip(typeFactory), typeFactory);
  }

  private ArrayType createArrayType(Type elemType, TypeFactory typeFactory) {
    if (elemType() == elemType) {
      return this;
    } else {
      return typeFactory.array(elemType);
    }
  }

  @Override
  protected boolean inequalByConstruction(Type that, Side side,
      BiFunction<Type, Type, Function<Side, Boolean>> f) {
    return that instanceof ArrayType thatArray
        && f.apply(this.elemType, thatArray.elemType).apply(side);
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
