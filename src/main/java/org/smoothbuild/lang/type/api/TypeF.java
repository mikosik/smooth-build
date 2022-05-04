package org.smoothbuild.lang.type.api;

import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * TypeFactory.
 */
public interface TypeF<T extends Type> {
  public AnyT any();

  public NothingT nothing();

  public ArrayT array(T elemType);

  public FuncT func(VarSet<T> tParams, T resT, ImmutableList<T> paramTs);

  public TupleT tuple(ImmutableList<T> items);

  public Var var(String name);

  public VarSet<?> varSet(Set<T> elements);

  public Bounded<T> bounded(Var var, Sides<T> sides);

  public VarBounds<T> varBounds(ImmutableMap<Var, Bounded<T>> map);

  public default T edge(Side side) {
    return switch (side) {
      case LOWER -> (T) nothing();
      case UPPER -> (T) any();
    };
  }

  public default Sides<T> oneSideBound(Side side, T type) {
    return switch (side) {
      case LOWER -> new Sides<>(type, (T) any());
      case UPPER -> new Sides<>((T) nothing(), type);
    };
  }
}
