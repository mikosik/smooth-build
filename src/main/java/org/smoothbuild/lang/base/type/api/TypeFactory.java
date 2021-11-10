package org.smoothbuild.lang.base.type.api;

import org.smoothbuild.lang.base.type.api.Sides.Side;

import com.google.common.collect.ImmutableList;

public interface TypeFactory<T extends Type> {
  public Bounds<T> unbounded();

  public Bounds<T> oneSideBound(Side<T> side, T type);

  public Side<T> upper();

  public Side<T> lower();

  public T any();

  public ArrayType array(T elemType);

  public T blob();

  public T bool();

  public FunctionType function(T result, ImmutableList<T> parameters);

  public T int_();

  public T nothing();

  public T string();

  public T variable(String name);
}
