package org.smoothbuild.lang.base.type.api;

import org.smoothbuild.lang.base.type.api.Sides.Side;

import com.google.common.collect.ImmutableList;

public interface TypeFactory<T extends Type> {
  public Bounds<T> unbounded();

  public Bounds<T> oneSideBound(Side<T> side, T type);

  public Side<T> upper();

  public Side<T> lower();

  public Type any();

  public ArrayType array(T elemType);

  public Type blob();

  public Type bool();

  public FunctionType function(T result, ImmutableList<T> parameters);

  public Type int_();

  public Type nothing();

  public Type string();

  public Variable variable(String name);
}
