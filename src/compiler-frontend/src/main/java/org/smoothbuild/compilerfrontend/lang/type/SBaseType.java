package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.sVarSet;

import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public abstract sealed class SBaseType extends SType
    permits SBlobType, SBoolType, SIntType, SStringType {
  private final Fqn fqn;

  protected SBaseType(Fqn fqn) {
    super(sVarSet());
    this.fqn = fqn;
  }

  public Fqn fqn() {
    return fqn;
  }

  @Override
  public String specifier(SVarSet localVars) {
    return fqn.toString();
  }
}
