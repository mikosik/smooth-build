package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.collect.Maybe.none;

public final class PConstructor extends PNamedFunc {
  private final PStruct pStruct;

  public PConstructor(PStruct pStruct) {
    super(
        new PExplicitType(pStruct.nameText(), pStruct.location()),
        pStruct.nameText(),
        pStruct.fields(),
        none(),
        none(),
        pStruct.location());
    setId(pStruct.id());
    this.pStruct = pStruct;
  }

  public PStruct pStruct() {
    return pStruct;
  }
}
