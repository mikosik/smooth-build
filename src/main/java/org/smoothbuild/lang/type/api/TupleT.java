package org.smoothbuild.lang.type.api;

import com.google.common.collect.ImmutableList;

public non-sealed interface TupleT extends ComposedT {
  public ImmutableList<? extends Type> items();
}
