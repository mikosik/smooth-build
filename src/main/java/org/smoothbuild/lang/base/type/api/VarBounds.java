package org.smoothbuild.lang.base.type.api;

import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import com.google.common.collect.ImmutableMap;

public record VarBounds<T extends Type>(ImmutableMap<Var, Bounded<T>> map) {
  private static final VarBounds<?> EMPTY = new VarBounds<>(ImmutableMap.of());

  public static <R extends Type> VarBounds<R> varBounds() {
    return (VarBounds<R>) EMPTY;
  }

  public static <R extends Type> VarBounds<R> varBounds(Bounded<R> bounded) {
    return new VarBounds<>(ImmutableMap.of(bounded.var(), bounded));
  }

  @Override
  public String toString() {
    return toCommaSeparatedString(map.values());
  }
}
