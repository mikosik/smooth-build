package org.smoothbuild.lang.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.type.TNamesS.funcTypeName;
import static org.smoothbuild.util.collect.Lists.concat;

import java.util.Objects;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public final class FuncTS extends TypeS implements ComposedTS {
  private final VarSetS tParams;
  private final TypeS res;
  private final ImmutableList<TypeS> params;

  public FuncTS(VarSetS tParams, TypeS res, ImmutableList<TypeS> params) {
    super(funcTypeName(tParams, res, params), calculateFuncVars(res, params));
    this.tParams = tParams;
    this.res = requireNonNull(res);
    this.params = requireNonNull(params);
  }

  public static VarSetS calculateFuncVars(TypeS resT, ImmutableList<TypeS> paramTs) {
    return calculateVars(concat(resT, paramTs));
  }

  public VarSetS tParams() {
    return tParams;
  }

  public TypeS res() {
    return res;
  }

  public ImmutableList<TypeS> params() {
    return params;
  }

  @Override
  public ImmutableList<TypeS> covars() {
    return ImmutableList.of(res);
  }

  @Override
  public ImmutableList<TypeS> contravars() {
    return params();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof FuncTS that
        && tParams.equals(that.tParams)
        && res.equals(that.res)
        && params.equals(that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tParams, res, params);
  }
}
