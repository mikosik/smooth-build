package org.smoothbuild.lang.expr;

import java.util.function.IntFunction;

import org.smoothbuild.lang.runtime.RuntimeTypes;
import org.smoothbuild.lang.type.ConcreteType;

@FunctionalInterface
public interface TypeChooser {

  public abstract ConcreteType choose(IntFunction<ConcreteType> childrenType);

  public static TypeChooser fixedTypeChooser(ConcreteType type) {
    return (IntFunction<ConcreteType> childrenType) -> type;
  }

  public static TypeChooser arrayOfFirstChildType(RuntimeTypes types) {
    return childrenType -> types.array(childrenType.apply(0));
  }
}
