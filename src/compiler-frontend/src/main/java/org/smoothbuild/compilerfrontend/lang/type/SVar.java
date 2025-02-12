package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.sVarSet;

import org.smoothbuild.compilerfrontend.lang.base.Identifiable;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

/**
 * Type variable.
 * This class is immutable.
 */
public final class SVar extends SType implements Identifiable {
  private static final String FLEXIBLE_VAR_PREFIX = "T~";
  private final SVarSet vars;
  private final Fqn fqn;
  private final boolean isFlexible;

  public SVar(Fqn fqn) {
    this(fqn, false);
  }

  private SVar(Fqn fqn, boolean isFlexible) {
    super(null);
    this.fqn = fqn;
    this.vars = sVarSet(this);
    this.isFlexible = isFlexible;
  }

  public static SVar flexibleVar(int i) {
    return new SVar(Fqn.fqn(FLEXIBLE_VAR_PREFIX + i), true);
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
  public boolean isFlexibleVar() {
    return isFlexible;
  }

  @Override
  public String specifier(SVarSet localVars) {
    return localVars.contains(this) ? name().toString() : fqn.toString();
  }
}
