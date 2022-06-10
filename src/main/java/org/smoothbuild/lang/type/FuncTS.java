package org.smoothbuild.lang.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.type.TNamesS.funcTypeName;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.Objects;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public final class FuncTS extends TypeS implements ComposedTS {
  private final TypeS res;
  private final ImmutableList<TypeS> params;

  public FuncTS(TypeS res, ImmutableList<TypeS> params) {
    super(funcTypeName(res, params), calculateFuncVars(res, params));
    this.res = requireNonNull(res);
    this.params = requireNonNull(params);
  }

  public static VarSetS calculateFuncVars(TypeS resT, ImmutableList<TypeS> paramTs) {
    return calculateVars(concat(resT, paramTs));
  }

  @Override
  public boolean includes(TypeS type) {
    return this.equals(type)
        || res.includes(type)
        || params.stream().anyMatch(p -> p.includes(type));
  }

  @Override
  public TypeS mapVars(Function<VarS, VarS> varMapper) {
    if (vars().isEmpty()) {
      return this;
    } else {
      return new FuncTS(res.mapVars(varMapper), map(params, t -> t.mapVars(varMapper)));
    }
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
        && res.equals(that.res)
        && params.equals(that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(res, params);
  }
}
