package org.smoothbuild.lang.base.type.api;

import org.smoothbuild.lang.base.type.api.Sides.Side;

import com.google.common.collect.ImmutableList;

public interface TypeFactory<T extends Type> {
  public Bounds<T> unbounded();

  public Bounds<T> oneSideBound(Side<T> side, T type);

  public Side<T> upper();

  public Side<T> lower();

  public ArrayType array(T elemType);

  public FuncType abstFunc(T result, ImmutableList<T> params);
}
