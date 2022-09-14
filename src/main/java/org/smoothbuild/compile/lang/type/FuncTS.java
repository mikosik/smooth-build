package org.smoothbuild.compile.lang.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.compile.lang.type.TNamesS.funcTypeName;
import static org.smoothbuild.compile.lang.type.VarSetS.varSetS;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public final class FuncTS extends TypeS {
  private final TypeS res;
  private final ImmutableList<TypeS> params;

  public FuncTS(TypeS res, List<? extends TypeS> params) {
    super(funcTypeName(res, params), calculateFuncVars(res, params));
    this.res = requireNonNull(res);
    this.params = ImmutableList.copyOf(params);
  }

  public static VarSetS calculateFuncVars(TypeS resT, List<? extends TypeS> paramTs) {
    return varSetS(concat(resT, paramTs));
  }

  @Override
  public TypeS mapComponents(Function<TypeS, TypeS> mapper) {
    return new FuncTS(mapper.apply(res), map(params, mapper));
  }

  @Override
  public FuncTS mapVars(Function<VarS, TypeS> varMapper) {
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
