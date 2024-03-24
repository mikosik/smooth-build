package org.smoothbuild.compilerfrontend.lang.type;

/**
 * Type variable.
 * This class is immutable.
 */
public sealed class SVar extends SType permits STempVar {
  private final SVarSet vars;

  public SVar(String name) {
    super(name, null);
    this.vars = SVarSet.varSetS(this);
  }

  @Override
  public SVarSet vars() {
    return vars;
  }

  public boolean isTemporary() {
    return this instanceof STempVar;
  }
}
