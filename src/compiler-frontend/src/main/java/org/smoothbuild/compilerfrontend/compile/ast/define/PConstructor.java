package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.collect.Maybe.none;

public final class PConstructor extends PNamedFunc {
  public PConstructor(PStruct pStruct) {
    super(
        createResultType(pStruct),
        pStruct.nameText(),
        pStruct.fields(),
        none(),
        none(),
        pStruct.location());
    setFqn(pStruct.fqn());
  }

  private static PTypeReference createResultType(PStruct pStruct) {
    var type = new PTypeReference(pStruct.nameText(), pStruct.location());
    type.setFqn(pStruct.fqn());
    type.setReferenced(pStruct);
    return type;
  }
}
