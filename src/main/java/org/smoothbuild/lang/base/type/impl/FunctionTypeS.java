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
  private final ImmutableList<TypeS> parameters;

  public FunctionTypeS(TypeS result, ImmutableList<TypeS> parameters) {
    super(functionTypeName(result, parameters), calculateVariables(result, parameters));
    this.result = requireNonNull(result);
    this.parameters = requireNonNull(parameters);
  }

  public static ImmutableSet<VariableS> calculateVariables(
      TypeS resultType, ImmutableList<TypeS> parameters) {
    return concat(resultType, parameters).stream()
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
  public ImmutableList<TypeS> parameters() {
    return parameters;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof FunctionTypeS that
        && result.equals(that.result)
        && parameters.equals(that.parameters);
  }
}
