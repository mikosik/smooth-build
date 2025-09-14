package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.common.collect.Set.set;

import org.smoothbuild.common.collect.Set;
import org.smoothbuild.compilerfrontend.lang.base.Identifiable;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public abstract sealed class SBaseType extends SType implements Identifiable
    permits SBlobType, SBoolType, SIntType, SStringType {
  private final Fqn fqn;

  protected SBaseType(Fqn fqn) {
    this.fqn = fqn;
  }

  @Override
  public Fqn fqn() {
    return fqn;
  }

  @Override
  protected Set<STypeVar> calculateTypeVars() {
    return set();
  }

  @Override
  public String specifier() {
    return fqn.toString();
  }
}
