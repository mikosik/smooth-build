package org.smoothbuild.compilerfrontend.lang.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.compilerfrontend.lang.type.STypeVarSet.sTypeVarSet;

import java.util.Objects;
import org.smoothbuild.common.collect.List;

/**
 * This class is immutable.
 */
public final class SFuncType extends SType {
  private final SType result;
  private final STupleType params;

  public SFuncType(List<SType> paramTs, SType resultT) {
    this(new STupleType(paramTs), resultT);
  }

  public SFuncType(STupleType params, SType result) {
    super(calculateFuncTypeVars(params, result));
    this.result = requireNonNull(result);
    this.params = requireNonNull(params);
  }

  public static STypeVarSet calculateFuncTypeVars(STupleType paramTs, SType resultT) {
    return sTypeVarSet(list(resultT).addAll(paramTs.elements()));
  }

  public STupleType params() {
    return params;
  }

  public SType result() {
    return result;
  }

  @Override
  public String specifier(STypeVarSet localTypeVars) {
    var paramStrings = params.elements().map(t -> t.specifier(localTypeVars));
    return "(" + paramStrings.toString(",") + ")->" + result.specifier(localTypeVars);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SFuncType that
        && params.equals(that.params)
        && result.equals(that.result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(params, result);
  }
}
