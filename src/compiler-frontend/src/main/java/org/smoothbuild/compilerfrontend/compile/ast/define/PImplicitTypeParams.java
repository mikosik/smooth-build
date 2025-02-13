package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

public final class PImplicitTypeParams implements PTypeParams {
  @Override
  public List<STypeVar> toTypeVarList() {
    return list();
  }
}
