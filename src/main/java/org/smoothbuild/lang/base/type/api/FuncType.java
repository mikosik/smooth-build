package org.smoothbuild.lang.base.type.api;

import com.google.common.collect.ImmutableList;

public interface FuncType extends Type {
  public Type res();

  public ImmutableList<? extends Type> params();
}
