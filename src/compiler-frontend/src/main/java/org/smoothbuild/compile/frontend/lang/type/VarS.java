package org.smoothbuild.compile.frontend.lang.type;

import static org.smoothbuild.compile.frontend.lang.type.VarSetS.varSetS;

/**
 * Type variable.
 * This class is immutable.
 */
public sealed class VarS extends TypeS permits TempVarS {
  private final VarSetS vars;

  public VarS(String name) {
    super(name, null);
    this.vars = varSetS(this);
  }

  @Override
  public VarSetS vars() {
    return vars;
  }

  public boolean isTemporary() {
    return this instanceof TempVarS;
  }
}
