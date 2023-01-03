package org.smoothbuild.compile.fs.ps.ast.define;

import java.util.Optional;

public final class ConstructorP extends NamedFuncP {
  public ConstructorP(StructP structP) {
    super(
        Optional.of(new TypeP(structP.name(), structP.location())),
        structP.name(),
        structP.name(),
        structP.fields(),
        Optional.empty(),
        Optional.empty(),
        structP.location());
  }
}
