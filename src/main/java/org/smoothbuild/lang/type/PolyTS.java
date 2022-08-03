package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.function.Function;

/**
 * Polymorphic type.
 */
public sealed abstract class PolyTS implements TypeS
    permits PolyFuncTS {
  private final String name;
  private final VarSetS quantifiedVars;
  private final MonoTS type;

  public PolyTS(VarSetS quantifiedVars, MonoTS type) {
    assertQuantifiedVarsArePresentInType(quantifiedVars, type);
    this.name = calculateName(quantifiedVars, type);
    this.quantifiedVars = requireNonNull(quantifiedVars);
    this.type = requireNonNull(type);
  }

  private static void assertQuantifiedVarsArePresentInType(VarSetS freeVars, MonoTS type) {
    checkArgument(type.vars().containsAll(freeVars),
        "Free variable(s) " + freeVars + " are not present in type " + type.q() + ".");
  }

  private static String calculateName(VarSetS freeVars, MonoTS type) {
    return freeVars.toString() + type.name();
  }

  @Override
  public String name() {
    return name;
  }

  public VarSetS quantifiedVars() {
    return quantifiedVars;
  }

  public MonoTS mono() {
    return type;
  }

  @Override
  public MonoTS mapQuantifiedVars(Function<VarS, VarS> varMapper) {
    return type.mapVars(v -> quantifiedVars.contains(v) ? varMapper.apply(v) : v);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PolyTS polyFuncS
        && quantifiedVars.equals(polyFuncS.quantifiedVars)
        && type.equals(polyFuncS.type);
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
