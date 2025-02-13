package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.common.collect.Set.set;

import org.smoothbuild.common.collect.Collection;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public abstract sealed class SBaseType extends SType
    permits SBlobType, SBoolType, SIntType, SStringType {
  private final Fqn fqn;

  protected SBaseType(Fqn fqn) {
    super(set());
    this.fqn = fqn;
  }

  public Fqn fqn() {
    return fqn;
  }

  @Override
  public String specifier(Collection<STypeVar> localTypeVars) {
    return fqn.toString();
  }
}
