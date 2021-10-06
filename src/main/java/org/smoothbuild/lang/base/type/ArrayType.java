package org.smoothbuild.lang.base.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.Lists.list;

import java.util.Map;

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

  @Override
  protected Type mergeImpl(Type other, Side direction, TypeFactory typeFactory) {
    if (other instanceof ArrayType that) {
      var elemA = this.elemType();
      var elemB = that.elemType();
      var elemM = elemA.merge(elemB, direction, typeFactory);
      if (elemA == elemM) {
        return this;
      } else if (elemB == elemM) {
        return that;
      } else {
        return typeFactory.array(elemM);
      }
    } else {
      return direction.edge();
    }
  }

  private ArrayType createArrayType(Type elemType, TypeFactory typeFactory) {
    if (elemType() == elemType) {
      return this;
    } else {
      return typeFactory.array(elemType);
    }
  }

  @Override
  public void inferVariableBounds(Type source, Side side, TypeFactory typeFactory,
      Map<Variable, Bounded> result) {
    if (source.equals(side.edge())) {
      this.elemType.inferVariableBounds(side.edge(), side, typeFactory, result);
    } else if (source instanceof ArrayType that) {
      this.elemType.inferVariableBounds(that.elemType, side, typeFactory, result);
    }
  }

  @Override
  protected boolean inequalByConstruction(Type that, Side side, InequalFunction isInequal) {
    return that instanceof ArrayType thatArray
        && isInequal.apply(this.elemType, thatArray.elemType, side);
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
