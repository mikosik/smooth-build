package org.smoothbuild.lang.type.api;

import org.smoothbuild.lang.type.api.Sides.Side;

import com.google.common.collect.ImmutableList;

/**
 * TypeFactory.
 */
public interface TypeF<T extends Type> {
  public Bounds<T> unbounded();

  public Bounds<T> oneSideBound(Side<T> side, T type);

  public Side<T> upper();

  public Side<T> lower();

  public ArrayT array(T elemType);

  public FuncT func(T result, ImmutableList<T> params);

  public TupleT tuple(ImmutableList<T> items);

  public OpenVarT oVar(String name);

  public ClosedVarT cVar(String name);
}
