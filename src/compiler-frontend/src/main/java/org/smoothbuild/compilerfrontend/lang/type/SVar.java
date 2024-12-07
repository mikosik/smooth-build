package org.smoothbuild.compilerfrontend.lang.type;

/**
 * Type variable.
 * This class is immutable.
 */
public final class SVar extends SType {
  private final SVarSet vars;

  public SVar(String name) {
    super(name, null);
    this.vars = SVarSet.varSetS(this);
  }

  public static SVar flexibleVar(int i) {
    return new SVar("T" + i);
  }

  @Override
  public SVarSet vars() {
    return vars;
  }

  @Override
  public boolean isFlexibleVar() {
    return name().chars().anyMatch(Character::isDigit);
  }
}
