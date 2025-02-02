package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;

import org.smoothbuild.compilerfrontend.lang.name.Fqn;

/**
 * Type variable.
 * This class is immutable.
 */
public final class SVar extends SType {
  private final SVarSet vars;
  private final Fqn fqn;

  public SVar(Fqn fqn) {
    super(fqn.toString(), null);
    this.fqn = fqn;
    this.vars = SVarSet.varSetS(this);
  }

  public static SVar flexibleVar(int i) {
    return new SVar(Fqn.fqn("T" + i));
  }

  public Fqn fqn() {
    return fqn;
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
