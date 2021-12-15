package org.smoothbuild.lang.base.type.api;

import com.google.common.collect.ImmutableList;

public non-sealed interface TupleT extends Type {
  public ImmutableList<? extends Type> items();
}
