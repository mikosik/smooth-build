package org.smoothbuild.compile.fs.ps.ast.expr;

import java.util.Optional;

import org.smoothbuild.compile.fs.ps.ast.type.TypeP;

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
