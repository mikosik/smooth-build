package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.type.ConcreteType;

@FunctionalInterface
public interface EvaluatorTypeChooser {

  public abstract ConcreteType choose();

  public static EvaluatorTypeChooser fixedTypeChooser(ConcreteType type) {
    return () -> type;
  }
}
