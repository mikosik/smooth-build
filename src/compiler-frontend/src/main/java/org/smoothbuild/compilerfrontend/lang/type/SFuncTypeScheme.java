package org.smoothbuild.compilerfrontend.lang.type;

import org.smoothbuild.common.collect.List;

/**
 * Polymorphic function type (= function type scheme).
 */
public final class SFuncTypeScheme extends STypeScheme {
  public SFuncTypeScheme(List<STypeVar> typeParams, SFuncType sFuncType) {
    super(typeParams, sFuncType);
  }

  @Override
  public SFuncType type() {
    return (SFuncType) super.type();
  }
}
