package org.smoothbuild.lang.base.type.impl;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.type.api.TypeNames.functionTypeName;
import static org.smoothbuild.util.collect.Lists.concat;

import java.util.Collection;

import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * This class is immutable.
 */
public final class FunctionTypeS extends TypeS implements FunctionType {
  private final TypeS result;
  private final ImmutableList<TypeS> params;

  public FunctionTypeS(TypeS result, ImmutableList<TypeS> params) {
    super(functionTypeName(result, params), calculateVariables(result, params));
    this.result = requireNonNull(result);
    this.params = requireNonNull(params);
  }

  public static ImmutableSet<VariableS> calculateVariables(
      TypeS resultType, ImmutableList<TypeS> params) {
    return concat(resultType, params).stream()
        .map(TypeS::variables)
        .flatMap(Collection::stream)
        .sorted(comparing(Type::name))
        .collect(toImmutableSet());
  }

  @Override
  public TypeS result() {
    return result;
  }

  @Override
  public ImmutableList<TypeS> params() {
    return params;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof FunctionTypeS that
        && result.equals(that.result)
        && params.equals(that.params);
  }
}
