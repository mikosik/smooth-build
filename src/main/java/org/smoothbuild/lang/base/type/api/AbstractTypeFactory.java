package org.smoothbuild.lang.base.type.api;

import org.smoothbuild.lang.base.type.api.Sides.Side;

public abstract class AbstractTypeFactory<T extends Type> implements TypeFactory<T> {
  @Override
  public Bounds<T> unbounded() {
    return new Bounds<>(nothing(), any());
  }

  @Override
  public Bounds<T> oneSideBound(Side<T> side, T type) {
    return side.dispatch(
        () -> new Bounds<>(type, any()),
        () -> new Bounds<>(nothing(), type)
    );
  }
}
