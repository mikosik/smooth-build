package org.smoothbuild.lang.base.type.api;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public interface StructType extends Type {
  public ImmutableList<? extends Type> fields();

  public ImmutableList<String> names();

  public ImmutableMap<String, Integer> nameToIndex();

  public static ImmutableSet<Variable> calculateVariables(
      ImmutableList<? extends Type> fields) {
    return fields.stream()
        .map(Type::variables)
        .flatMap(Collection::stream)
        .sorted(comparing(Type::name))
        .collect(toImmutableSet());
  }
}
