package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.common.collect.Set.set;

import org.smoothbuild.compilerfrontend.lang.base.Identifiable;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public abstract sealed class SBaseType extends SType implements Identifiable
    permits SBlobType, SBoolType, SIntType, SStringType {
  private final Fqn fqn;

  protected SBaseType(Fqn fqn) {
    super(set());
    this.fqn = fqn;
  }

  @Override
  public Fqn fqn() {
    return fqn;
  }

  @Override
  public String specifier() {
    return fqn.toString();
  }
}
