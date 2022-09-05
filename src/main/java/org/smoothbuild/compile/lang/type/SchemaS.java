package org.smoothbuild.compile.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.function.Function;

/**
 * Polymorphic type (aka type schema).
 */
public sealed class SchemaS implements TypelikeS
    permits FuncSchemaS {
  private final String name;
  private final VarSetS quantifiedVars;
  private final TypeS type;

  public SchemaS(VarSetS quantifiedVars, TypeS type) {
    assertQuantifiedVarsArePresentInType(quantifiedVars, type);
    this.name = calculateName(quantifiedVars, type);
    this.quantifiedVars = requireNonNull(quantifiedVars);
    this.type = requireNonNull(type);
  }

  private static void assertQuantifiedVarsArePresentInType(VarSetS quantifiedVars, TypeS type) {
    checkArgument(type.vars().containsAll(quantifiedVars),
        "Quantified variable(s) " + quantifiedVars + " are not present in type " + type.q() + ".");
  }

  private static String calculateName(VarSetS quantifiedVars, TypeS type) {
    return quantifiedVars.toString() + type.name();
  }

  @Override
  public String name() {
    return name;
  }

  public VarSetS quantifiedVars() {
    return quantifiedVars;
  }

  public TypeS type() {
    return type;
  }

  public TypeS monoize(Function<VarS, TypeS> mapper) {
    return type.mapVars(v -> quantifiedVars.contains(v) ? mapper.apply(v) : v);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof SchemaS schema
        && quantifiedVars.equals(schema.quantifiedVars)
        && type.equals(schema.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, quantifiedVars, type);
  }

  @Override
  public String toString() {
    return name;
  }
}
