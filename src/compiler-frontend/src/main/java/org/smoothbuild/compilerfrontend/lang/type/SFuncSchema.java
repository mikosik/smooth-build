package org.smoothbuild.compilerfrontend.lang.type;

import org.smoothbuild.common.collect.List;

/**
 * Polymorphic function type (= function type schema).
 */
public final class SFuncSchema extends SSchema {
  public SFuncSchema(List<STypeVar> typeParams, SFuncType sFuncType) {
    super(typeParams, sFuncType);
  }

  @Override
  public SFuncType type() {
    return (SFuncType) super.type();
  }
}
