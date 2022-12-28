package org.smoothbuild.compile.fs.lang.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.compile.fs.lang.base.TypeNamesS.funcTypeName;
import static org.smoothbuild.compile.fs.lang.type.VarSetS.varSetS;
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

  public FuncTS(ImmutableList<TypeS> paramTs, TypeS resT) {
    this(new TupleTS(paramTs), resT);
  }

  public FuncTS(TupleTS params, TypeS res) {
    super(funcTypeName(params, res), calculateFuncVars(params, res));
    this.res = requireNonNull(res);
    this.params = requireNonNull(params);
  }

  public static VarSetS calculateFuncVars(TupleTS paramTs, TypeS resT) {
    return varSetS(concat(resT, paramTs.items()));
  }

  @Override
  public TypeS mapComponents(Function<TypeS, TypeS> mapper) {
    return new FuncTS(params.mapComponents(mapper), mapper.apply(res));
  }

  @Override
  public FuncTS mapVars(Function<VarS, TypeS> varMapper) {
    if (vars().isEmpty()) {
      return this;
    } else {
      return new FuncTS(params.mapVars(varMapper), res.mapVars(varMapper));
    }
  }

  public TupleTS params() {
    return params;
  }

  public TypeS res() {
    return res;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof FuncTS that
        && params.equals(that.params)
        && res.equals(that.res);
  }

  @Override
  public int hashCode() {
    return Objects.hash(params, res);
  }
}
