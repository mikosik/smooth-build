package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.collect.Maybe.none;

public final class PConstructor extends PNamedFunc {
  public PConstructor(PStruct pStruct) {
    super(
        new PExplicitType(pStruct.name(), pStruct.location()),
        pStruct.name(),
        pStruct.name(),
        pStruct.fields(),
        none(),
        none(),
        pStruct.location());
  }
}
