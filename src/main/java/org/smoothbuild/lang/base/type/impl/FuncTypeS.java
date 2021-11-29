package org.smoothbuild.lang.base.type.impl;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.type.api.TypeNames.funcTypeName;
import static org.smoothbuild.util.collect.Lists.concat;

import java.util.Collection;

import org.smoothbuild.lang.base.type.api.FuncType;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * This class is immutable.
 */
public final class FuncTypeS extends TypeS implements FuncType {
  private final TypeS result;
  private final ImmutableList<TypeS> params;

  public FuncTypeS(TypeS result, ImmutableList<TypeS> params) {
    super(funcTypeName(result, params), calculateVars(result, params));
    this.result = requireNonNull(result);
    this.params = requireNonNull(params);
  }

  public static ImmutableSet<VarS> calculateVars(
      TypeS resultType, ImmutableList<TypeS> params) {
    return concat(resultType, params).stream()
        .map(TypeS::vars)
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
    return object instanceof FuncTypeS that
        && result.equals(that.result)
        && params.equals(that.params);
  }
}
