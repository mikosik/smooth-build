package org.smoothbuild.compilerfrontend.lang.type;

/**
 * Type variable.
 * This class is immutable.
 */
public final class SVar extends SType {
  private static final String FLEXIBLE_VAR_PREFIX = "_";
  private final SVarSet vars;

  public SVar(String name) {
    super(name, null);
    this.vars = SVarSet.varSetS(this);
  }

  public static SVar newFlexibleVar(int i) {
    return new SVar(FLEXIBLE_VAR_PREFIX + i);
  }

  @Override
  public SVarSet vars() {
    return vars;
  }

  @Override
  public boolean isFlexibleVar() {
    return name().startsWith(FLEXIBLE_VAR_PREFIX);
  }
}
