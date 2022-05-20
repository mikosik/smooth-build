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
  private final VarSetS freeVars;
  private final MonoTS type;

  public PolyTS(VarSetS freeVars, MonoTS type) {
    assertFreeVarsArePresentInType(freeVars, type);
    this.name = calculateName(freeVars, type);
    this.freeVars = requireNonNull(freeVars);
    this.type = requireNonNull(type);
  }

  private static void assertFreeVarsArePresentInType(VarSetS freeVars, MonoTS type) {
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

  public VarSetS freeVars() {
    return freeVars;
  }

  public MonoTS mono() {
    return type;
  }

  @Override
  public MonoTS mapFreeVars(Function<VarS, VarS> varMapper) {
    return type.mapVars(v -> freeVars.contains(v) ? varMapper.apply(v) : v);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PolyTS polyFuncS
        && freeVars.equals(polyFuncS.freeVars)
        && type.equals(polyFuncS.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, freeVars, type);
  }

  @Override
  public String toString() {
    return name;
  }
}
