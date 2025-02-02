package org.smoothbuild.compilerfrontend.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.collect.Map.zipToMap;

import java.util.Objects;
import org.smoothbuild.common.collect.List;

/**
 * Polymorphic type (aka type schema).
 */
public sealed class SSchema permits SFuncSchema {
  private final SVarSet quantifiedVars;
  private final SType type;

  public SSchema(SVarSet quantifiedVars, SType type) {
    assertQuantifiedVarsArePresentInType(quantifiedVars, type);
    this.quantifiedVars = requireNonNull(quantifiedVars);
    this.type = requireNonNull(type);
  }

  private static void assertQuantifiedVarsArePresentInType(SVarSet quantifiedVars, SType type) {
    checkArgument(
        type.vars().containsAll(quantifiedVars),
        "Quantified variable(s) " + quantifiedVars + " are not present in type " + type.q() + ".");
  }

  /**
   * Type variables that are quantified with for-all quantifier.
   * Other variables present in this schema are type variables which are quantified variables
   * of enclosing environment (enclosing function or value).
   */
  public SVarSet quantifiedVars() {
    return quantifiedVars;
  }

  public SType type() {
    return type;
  }

  public SType instantiate(List<SType> typeArgs) {
    var map = zipToMap(quantifiedVars.toList(), typeArgs);
    return type.mapVars(v -> map.getOrDefault(v, v));
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SSchema schema
        && quantifiedVars.equals(schema.quantifiedVars)
        && type.equals(schema.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(quantifiedVars, type);
  }

  @Override
  public String toString() {
    return quantifiedVars.toString() + type.specifier();
  }
}
