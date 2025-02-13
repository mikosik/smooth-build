package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.compilerfrontend.lang.type.STypeVarSet.sTypeVarSet;

import org.smoothbuild.compilerfrontend.lang.base.Identifiable;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

/**
 * Type variable.
 * This class is immutable.
 */
public final class STypeVar extends SType implements Identifiable {
  private static final String FLEXIBLE_VAR_PREFIX = "T~";
  private final STypeVarSet typeVars;
  private final Fqn fqn;
  private final boolean isFlexible;

  public STypeVar(Fqn fqn) {
    this(fqn, false);
  }

  private STypeVar(Fqn fqn, boolean isFlexible) {
    super(null);
    this.fqn = fqn;
    this.typeVars = sTypeVarSet(this);
    this.isFlexible = isFlexible;
  }

  public static STypeVar flexibleTypeVar(int i) {
    return new STypeVar(Fqn.fqn(FLEXIBLE_VAR_PREFIX + i), true);
  }

  @Override
  public Fqn fqn() {
    return fqn;
  }

  @Override
  public STypeVarSet typeVars() {
    return typeVars;
  }

  @Override
  public boolean isFlexibleTypeVar() {
    return isFlexible;
  }

  @Override
  public String specifier(STypeVarSet localTypeVars) {
    return localTypeVars.contains(this) ? name().toString() : fqn.toString();
  }
}
