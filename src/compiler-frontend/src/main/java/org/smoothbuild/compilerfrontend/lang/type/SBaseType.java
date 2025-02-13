package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.compilerfrontend.lang.type.STypeVarSet.sTypeVarSet;

import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public abstract sealed class SBaseType extends SType
    permits SBlobType, SBoolType, SIntType, SStringType {
  private final Fqn fqn;

  protected SBaseType(Fqn fqn) {
    super(sTypeVarSet());
    this.fqn = fqn;
  }

  public Fqn fqn() {
    return fqn;
  }

  @Override
  public String specifier(STypeVarSet localTypeVars) {
    return fqn.toString();
  }
}
