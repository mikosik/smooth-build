package org.smoothbuild.lang.base.type.api;

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
}
