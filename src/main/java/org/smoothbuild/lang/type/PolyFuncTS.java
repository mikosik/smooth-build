package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Polymorphic type.
 */
public final class PolyFuncTS {
  private final String name;
  private final VarSetS freeVars;
  private final TypeS type;

  public PolyFuncTS(VarSetS freeVars, TypeS type) {
    this.name = calculateName(type, freeVars);
    this.freeVars = requireNonNull(freeVars);
    this.type = requireNonNull(type);
    checkArgument(type.vars().containsAll(freeVars),
        "Free variable(s) " + freeVars + " are not present in type " + type.q() + ".");
  }

  public String name() {
    return name;
  }

  public VarSetS freeVars() {
    return freeVars;
  }

  public TypeS type() {
    return type;
  }

  private static String calculateName(TypeS type, VarSetS freeVars) {
    return freeVars.toString() + type.toString();
  }
}
