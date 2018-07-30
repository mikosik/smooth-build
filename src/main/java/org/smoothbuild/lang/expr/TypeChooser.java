package org.smoothbuild.lang.expr;

import java.util.function.IntFunction;

import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.Type;

@FunctionalInterface
public interface TypeChooser<T extends Type> {

  public abstract T choose(IntFunction<T> childrenType);

  public static TypeChooser<ConcreteType> fixedTypeChooser(ConcreteType type) {
    return (IntFunction<ConcreteType> childrenType) -> type;
  }

  public static TypeChooser<ConcreteType> arrayOfFirstChildType() {
    return childrenType -> childrenType.apply(0).increaseCoreDepthBy(1);
  }
}
