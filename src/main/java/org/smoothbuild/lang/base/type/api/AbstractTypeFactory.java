package org.smoothbuild.lang.base.type.api;

import org.smoothbuild.lang.base.type.api.Sides.Side;

import com.google.common.collect.ImmutableSet;

public abstract class AbstractTypeFactory implements TypeFactory {
  @Override
  public ImmutableSet<BaseType> inferableBaseTypes() {
    return ImmutableSet.<BaseType>builder()
        .addAll(baseTypes())
        .add(any())
        .build();
  }

  @Override
  public ImmutableSet<BaseType> baseTypes() {
    return ImmutableSet.of(
        blob(),
        bool(),
        int_(),
        nothing(),
        string()
    );
  }

  @Override
  public Bounds unbounded() {
    return new Bounds(nothing(), any());
  }

  @Override
  public Bounds oneSideBound(Side side, Type type) {
    return side.dispatch(
        () -> new Bounds(type, any()),
        () -> new Bounds(nothing(), type)
    );
  }
}
