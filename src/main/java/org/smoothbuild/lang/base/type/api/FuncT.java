package org.smoothbuild.lang.base.type.api;

import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;

public non-sealed interface FuncT extends ComposedT {
  public Type res();

  public ImmutableList<? extends Type> params();

  public static boolean calculateHasClosedVars(Type res, ImmutableList<? extends Type> params) {
    return anyMatch(res, params, Type::hasClosedVars);
  }

  public static boolean calculateHasOpenVars(Type res, ImmutableList<? extends Type> params) {
    return anyMatch(res, params, Type::hasOpenVars);
  }

  private static boolean anyMatch(Type res, ImmutableList<? extends Type> params,
      Predicate<Type> predicate) {
    return predicate.test(res) || params.stream().anyMatch(predicate);
  }
}
