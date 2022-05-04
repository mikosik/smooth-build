package org.smoothbuild.lang.type.api;

import com.google.common.collect.ImmutableMap;

public interface VarBounds<T extends Type> {
  public ImmutableMap<Var, Bounded<T>> map();
}
