package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.compilerfrontend.lang.name.Id;

public abstract sealed class SBaseType extends SType
    permits SBlobType, SBoolType, SIntType, SStringType {
  private final Id id;

  protected SBaseType(Id id) {
    this(id, varSetS());
  }

  protected SBaseType(Id id, SVarSet vars) {
    super(id.toString(), vars);
    this.id = id;
  }

  public Id id() {
    return id;
  }
}
