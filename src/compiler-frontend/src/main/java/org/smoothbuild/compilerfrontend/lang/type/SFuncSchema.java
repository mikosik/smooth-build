package org.smoothbuild.compilerfrontend.lang.type;

/**
 * Polymorphic function type (= function type schema).
 */
public final class SFuncSchema extends SSchema {
  public SFuncSchema(STypeVarSet typeParams, SFuncType sFuncType) {
    super(typeParams, sFuncType);
  }

  @Override
  public SFuncType type() {
    return (SFuncType) super.type();
  }
}
