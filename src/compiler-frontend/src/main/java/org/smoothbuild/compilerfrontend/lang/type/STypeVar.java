package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.sVarSet;

import org.smoothbuild.compilerfrontend.lang.base.Identifiable;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

/**
 * Type variable.
 * This class is immutable.
 */
public final class STypeVar extends SType implements Identifiable {
  private static final String FLEXIBLE_VAR_PREFIX = "T~";
  private final SVarSet vars;
  private final Fqn fqn;
  private final boolean isFlexible;

  public STypeVar(Fqn fqn) {
    this(fqn, false);
  }

  private STypeVar(Fqn fqn, boolean isFlexible) {
    super(null);
    this.fqn = fqn;
    this.vars = sVarSet(this);
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
  public SVarSet vars() {
    return vars;
  }

  @Override
  public boolean isFlexibleTypeVar() {
    return isFlexible;
  }

  @Override
  public String specifier(SVarSet localVars) {
    return localVars.contains(this) ? name().toString() : fqn.toString();
  }
}
