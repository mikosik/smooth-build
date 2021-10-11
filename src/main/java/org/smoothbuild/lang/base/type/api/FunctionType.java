package org.smoothbuild.lang.base.type.api;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;
import static org.smoothbuild.util.Lists.concat;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public interface FunctionType extends Type {
  public Type resultType();

  public ImmutableList<Type> parameters();

  public static ImmutableSet<Variable> calculateVariables(
      Type resultType, ImmutableList<Type> parameters) {
    return concat(resultType, parameters).stream()
        .map(Type::variables)
        .flatMap(Collection::stream)
        .sorted(comparing(Type::name))
        .collect(toImmutableSet());
  }
}
