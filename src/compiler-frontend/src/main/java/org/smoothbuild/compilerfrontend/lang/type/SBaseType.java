package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public abstract sealed class SBaseType extends SType
    permits SBlobType, SBoolType, SIntType, SStringType {
  private final Fqn fqn;

  protected SBaseType(Fqn fqn) {
    this(fqn, varSetS());
  }

  protected SBaseType(Fqn fqn, SVarSet vars) {
    super(fqn.toString(), vars);
    this.fqn = fqn;
  }

  public Fqn fqn() {
    return fqn;
  }
}
