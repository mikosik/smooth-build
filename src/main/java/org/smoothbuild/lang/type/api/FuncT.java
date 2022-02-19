package org.smoothbuild.lang.type.api;

import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;

public non-sealed interface FuncT extends ComposedT {
  public Type res();

  public ImmutableList<? extends Type> params();

  public static boolean calculateHasClosedVars(Type res, ImmutableList<? extends Type> params) {
    return anyMatch(res, params, Type::hasClosedVars);
  }

  private static boolean anyMatch(Type res, ImmutableList<? extends Type> params,
      Predicate<Type> predicate) {
    return predicate.test(res) || params.stream().anyMatch(predicate);
  }
}