package org.smoothbuild.compilerfrontend.lang.type;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.collect.Map.zipToMap;

import java.util.Objects;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.collect.List;

/**
 * Polymorphic type (aka type schema).
 */
public sealed class SSchema permits SFuncSchema {
  private final STypeVarSet typeParams;
  private final SType type;

  public SSchema(STypeVarSet typeParams, SType type) {
    assertTypeParamsArePresentInType(typeParams, type);
    this.typeParams = requireNonNull(typeParams);
    this.type = requireNonNull(type);
  }

  private static void assertTypeParamsArePresentInType(STypeVarSet typeParams, SType type) {
    if (!type.typeVars().containsAll(typeParams)) {
      throw new IllegalArgumentException(
          "Type parameter(s) " + typeParams + " are not present in type " + type.q() + ".");
    }
  }

  /**
   * Type variables that are quantified with for-all quantifier (= Parameters of this schema).
   * Other variables present in this schema are type variables which are quantified variables
   * of enclosing environment (enclosing function or value).
   */
  public STypeVarSet typeParams() {
    return typeParams;
  }

  public SType type() {
    return type;
  }

  public SType instantiate(List<SType> typeArgs) {
    var map = zipToMap(typeParams.toList(), typeArgs);
    return type.mapVars(v -> map.getOrDefault(v, v));
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SSchema schema
        && typeParams.equals(schema.typeParams)
        && type.equals(schema.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(typeParams, type);
  }

  public String q() {
    return Strings.q(toShortString());
  }

  public String toShortString() {
    return typeParams.toShortString() + type.specifier(typeParams);
  }

  @Override
  public String toString() {
    return typeParams.toString() + type.specifier();
  }
}
