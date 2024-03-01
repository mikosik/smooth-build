package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.collect.Maybe.none;

public final class ConstructorP extends NamedFuncP {
  public ConstructorP(StructP structP) {
    super(
        new ExplicitTP(structP.name(), structP.location()),
        structP.name(),
        structP.name(),
        structP.fields(),
        none(),
        none(),
        structP.location());
  }
}
