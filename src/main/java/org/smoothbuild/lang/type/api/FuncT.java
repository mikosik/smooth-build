package org.smoothbuild.lang.type.api;

import com.google.common.collect.ImmutableList;

public interface FuncT {
  public Type res();

  public ImmutableList<? extends Type> params();
}
