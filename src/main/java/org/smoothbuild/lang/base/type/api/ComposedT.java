package org.smoothbuild.lang.base.type.api;

import com.google.common.collect.ImmutableList;

public sealed interface ComposedT extends Type
    permits ArrayT, FuncT, TupleT {
  public ImmutableList<Type> covars();
  public ImmutableList<Type> contravars();
}
