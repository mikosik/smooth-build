package org.smoothbuild.lang.expr;

import java.util.function.IntFunction;

import org.smoothbuild.lang.type.ConcreteType;

@FunctionalInterface
public interface TypeChooser {

  public abstract ConcreteType choose(IntFunction<ConcreteType> childrenType);

  public static TypeChooser fixedTypeChooser(ConcreteType type) {
    return (IntFunction<ConcreteType> childrenType) -> type;
  }

  public static TypeChooser arrayOfFirstChildType() {
    return childrenType -> childrenType.apply(0).increaseCoreDepthBy(1);
  }
}
