package org.smoothbuild.compilerfrontend.lang.type;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Set;

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
    this.result = requireNonNull(result);
    this.params = requireNonNull(params);
  }

  @Override
  protected Set<STypeVar> calculateTypeVars() {
    return params.elements().flatMap(SType::typeVars).toSet().addAll(result.typeVars());
  }

  public STupleType params() {
    return params;
  }

  public SType result() {
    return result;
  }

  @Override
  public String specifier() {
    var paramStrings = params.elements().map(SType::specifier);
    return "(" + paramStrings.toString(",") + ")->" + result.specifier();
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
