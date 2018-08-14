package org.smoothbuild.lang.type;

import java.util.function.IntFunction;

@FunctionalInterface
public interface TypeChooser<T extends Type> {

  public abstract T choose(IntFunction<T> childrenType);

  public static <T extends Type> TypeChooser<T> fixedTypeChooser(T type) {
    return (IntFunction<T> childrenType) -> type;
  }

  public static TypeChooser<ConcreteType> arrayOfFirstChildType() {
    return childrenType -> childrenType.apply(0).increaseCoreDepthBy(1);
  }
}
