package org.smoothbuild.compile.fs.lang.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.compile.fs.lang.base.TypeNamesS.funcTypeName;
import static org.smoothbuild.compile.fs.lang.type.VarSetS.varSetS;
import static org.smoothbuild.util.collect.Lists.concat;

import java.util.Objects;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public final class FuncTS extends TypeS {
  private final TypeS result;
  private final TupleTS params;

  public FuncTS(ImmutableList<TypeS> paramTs, TypeS resultT) {
    this(new TupleTS(paramTs), resultT);
  }

  public FuncTS(TupleTS params, TypeS result) {
    super(funcTypeName(params, result), calculateFuncVars(params, result));
    this.result = requireNonNull(result);
    this.params = requireNonNull(params);
  }

  public static VarSetS calculateFuncVars(TupleTS paramTs, TypeS resultT) {
    return varSetS(concat(resultT, paramTs.items()));
  }

  public TupleTS params() {
    return params;
  }

  public TypeS result() {
    return result;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof FuncTS that
        && params.equals(that.params)
        && result.equals(that.result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(params, result);
  }
}
