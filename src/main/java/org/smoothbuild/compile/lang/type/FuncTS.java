package org.smoothbuild.compile.lang.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.compile.lang.type.TNamesS.funcTypeName;
import static org.smoothbuild.compile.lang.type.VarSetS.varSetS;
import static org.smoothbuild.util.collect.Lists.concat;

import java.util.Objects;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public final class FuncTS extends TypeS {
  private final TypeS res;
  private final TupleTS params;

  public FuncTS(TypeS resT, ImmutableList<TypeS> paramTs) {
    this(resT, new TupleTS(paramTs));
  }

  public FuncTS(TypeS res, TupleTS params) {
    super(funcTypeName(res, params), calculateFuncVars(res, params));
    this.res = requireNonNull(res);
    this.params = requireNonNull(params);
  }

  public static VarSetS calculateFuncVars(TypeS resT, TupleTS paramTs) {
    return varSetS(concat(resT, paramTs.items()));
  }

  @Override
  public TypeS mapComponents(Function<TypeS, TypeS> mapper) {
    return new FuncTS(mapper.apply(res), params.mapComponents(mapper));
  }

  @Override
  public FuncTS mapVars(Function<VarS, TypeS> varMapper) {
    if (vars().isEmpty()) {
      return this;
    } else {
      return new FuncTS(res.mapVars(varMapper), params.mapVars(varMapper));
    }
  }

  public TypeS res() {
    return res;
  }

  public TupleTS params() {
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

  @Override
  public int hashCode() {
    return Objects.hash(res, params);
  }
}
