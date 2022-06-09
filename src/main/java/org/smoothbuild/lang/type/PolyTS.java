package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Objects;

/**
 * Polymorphic type.
 */
public sealed abstract class PolyTS implements TKind
    permits PolyFuncTS {
  private final String name;
  private final VarSetS freeVars;
  private final TypeS type;

  public PolyTS(VarSetS freeVars, TypeS type) {
    checkArgument(type.vars().containsAll(freeVars),
        "Free variable(s) " + freeVars + " are not present in type " + type.q() + ".");
    this.name = calculateName(type, freeVars);
    this.freeVars = requireNonNull(freeVars);
    this.type = requireNonNull(type);
  }

  private static String calculateName(TypeS type, VarSetS freeVars) {
    return freeVars.toString() + type.name();
  }

  @Override
  public String name() {
    return name;
  }

  public VarSetS freeVars() {
    return freeVars;
  }

  public TypeS type() {
    return type;
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
