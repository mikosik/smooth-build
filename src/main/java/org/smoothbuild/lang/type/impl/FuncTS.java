package org.smoothbuild.lang.type.impl;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.type.api.TypeNames.funcTypeName;

import org.smoothbuild.lang.type.api.FuncT;
import org.smoothbuild.lang.type.api.Type;
import org.smoothbuild.util.collect.Lists;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public final class FuncTS extends TypeS implements FuncT {
  private final TypeS res;
  private final ImmutableList<TypeS> params;

  public FuncTS(TypeS res, ImmutableList<TypeS> params) {
    super(
        funcTypeName(res, params),
        calculateOpenVars(Lists.concat(res, params)),
        FuncT.calculateHasClosedVars(res, params));
    this.res = requireNonNull(res);
    this.params = requireNonNull(params);
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
  public ImmutableList<Type> covars() {
    return ImmutableList.of(res);
  }

  @Override
  public ImmutableList<Type> contravars() {
    return (ImmutableList<Type>)(Object) params();
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