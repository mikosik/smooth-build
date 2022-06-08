package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;

public final class ValN extends RefableObjN {
  private final Optional<TypeN> typeN;

  public ValN(Optional<TypeN> typeN, String name, Optional<ObjN> body,
      Optional<AnnN> annotation, Loc loc) {
    super(name, body, annotation, loc);
    this.typeN = typeN;
  }

  public Optional<TypeN> typeN() {
    return typeN;
  }

  @Override
  public Optional<TypeN> evalTN() {
    return typeN();
  }
}
