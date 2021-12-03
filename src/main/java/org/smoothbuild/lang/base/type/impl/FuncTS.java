package org.smoothbuild.lang.base.type.impl;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.type.api.TypeNames.funcTypeName;
import static org.smoothbuild.util.collect.Lists.concat;

import java.util.Collection;

import org.smoothbuild.lang.base.type.api.FuncT;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * This class is immutable.
 */
public final class FuncTS extends TypeS implements FuncT {
  private final TypeS res;
  private final ImmutableList<TypeS> params;

  public FuncTS(TypeS res, ImmutableList<TypeS> params) {
    super(funcTypeName(res, params), calculateVars(res, params));
    this.res = requireNonNull(res);
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
  public TypeS res() {
    return res;
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
    return object instanceof FuncTS that
        && res.equals(that.res)
        && params.equals(that.params);
  }
}
