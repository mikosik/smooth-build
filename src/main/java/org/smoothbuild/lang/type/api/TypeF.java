package org.smoothbuild.lang.type.api;

import com.google.common.collect.ImmutableList;

/**
 * TypeFactory.
 */
public interface TypeF<T extends Type> {
  public AnyT any();

  public NothingT nothing();

  public ArrayT array(T elemType);

  public FuncT func(T result, ImmutableList<T> params);

  public TupleT tuple(ImmutableList<T> items);

  public OpenVarT oVar(String name);

  public ClosedVarT cVar(String name);

  public default T edge(Side side) {
    return switch (side) {
      case LOWER -> (T) nothing();
      case UPPER -> (T) any();
    };
  }

  public default Bounds<T> oneSideBound(Side side, T type) {
    return switch (side) {
      case LOWER -> new Bounds<>(type, (T) any());
      case UPPER -> new Bounds<>((T) nothing(), type);
    };
  }
}
