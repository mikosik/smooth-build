package org.smoothbuild.lang.type.api;

import com.google.common.collect.ImmutableList;

public non-sealed interface FuncT extends ComposedT {
  public Type res();

  public ImmutableList<? extends Type> params();
}
