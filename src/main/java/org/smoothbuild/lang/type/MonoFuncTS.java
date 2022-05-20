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
public final class MonoFuncTS extends ComposedTS implements FuncTS {
  private final MonoTS res;
  private final ImmutableList<MonoTS> params;

  public MonoFuncTS(MonoTS res, ImmutableList<MonoTS> params) {
    super(funcTypeName(res, params), calculateFuncVars(res, params));
    this.res = requireNonNull(res);
    this.params = requireNonNull(params);
  }

  public static VarSetS calculateFuncVars(MonoTS resT, ImmutableList<MonoTS> paramTs) {
    return calculateVars(concat(resT, paramTs));
  }

  @Override
  public boolean includes(MonoTS type) {
    return this.equals(type)
        || res.includes(type)
        || params.stream().anyMatch(p -> p.includes(type));
  }

  @Override
  public MonoTS mapVars(Function<VarS, VarS> varMapper) {
    if (vars().isEmpty()) {
      return this;
    } else {
      return new MonoFuncTS(res.mapVars(varMapper), map(params, t -> t.mapVars(varMapper)));
    }
  }

  @Override
  public MonoTS res() {
    return res;
  }

  @Override
  public ImmutableList<MonoTS> params() {
    return params;
  }

  @Override
  public ImmutableList<MonoTS> covars() {
    return ImmutableList.of(res);
  }

  @Override
  public ImmutableList<MonoTS> contravars() {
    return params();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof MonoFuncTS that
        && res.equals(that.res)
        && params.equals(that.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(res, params);
  }
}
