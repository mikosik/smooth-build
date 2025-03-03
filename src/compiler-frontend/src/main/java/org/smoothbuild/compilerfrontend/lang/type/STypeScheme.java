package org.smoothbuild.compilerfrontend.lang.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.collect.Map.zipToMap;
import static org.smoothbuild.compilerfrontend.lang.type.STypeVar.typeParamsToSourceCode;

import java.util.Objects;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.collect.List;

/**
 * Polymorphic type (aka type scheme).
 */
public sealed class STypeScheme permits SFuncTypeScheme {
  private final List<STypeVar> typeParams;
  private final SType type;

  public STypeScheme(List<STypeVar> typeParams, SType type) {
    assertTypeParamsArePresentInType(typeParams, type);
    this.typeParams = requireNonNull(typeParams);
    this.type = requireNonNull(type);
  }

  private static void assertTypeParamsArePresentInType(List<STypeVar> typeParams, SType type) {
    if (!type.typeVars().containsAll(typeParams)) {
      throw new IllegalArgumentException(
          "Type parameter(s) " + typeParams + " are not present in type " + type.q() + ".");
    }
  }

  /**
   * Type variables that are quantified with for-all quantifier (= Parameters of this scheme).
   * Other variables present in this scheme are type variables which are quantified variables
   * of enclosing environment (enclosing function or value).
   */
  public List<STypeVar> typeParams() {
    return typeParams;
  }

  public SType type() {
    return type;
  }

  public SType instantiate(List<SType> typeArgs) {
    var map = zipToMap(typeParams.toList(), typeArgs);
    return type.mapTypeVars(v -> map.getOrDefault(v, v));
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof STypeScheme typeScheme
        && typeParams.equals(typeScheme.typeParams)
        && type.equals(typeScheme.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(typeParams, type);
  }

  public String q() {
    return Strings.q(toString());
  }

  @Override
  public String toString() {
    return typeParamsToSourceCode(typeParams) + type.specifier();
  }
}
