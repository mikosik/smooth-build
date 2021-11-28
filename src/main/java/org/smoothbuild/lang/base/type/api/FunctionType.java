package org.smoothbuild.lang.base.type.api;

import com.google.common.collect.ImmutableList;

public interface FunctionType extends Type {
  public Type result();

  public ImmutableList<? extends Type> params();
}
