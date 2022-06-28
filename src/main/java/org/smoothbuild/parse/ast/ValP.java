package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;

public final class ValP extends MonoRefableP implements MonoTopRefableP {
  private final Optional<TypeP> typeN;

  public ValP(Optional<TypeP> typeN, String name, Optional<ObjP> body,
      Optional<AnnP> annotation, Loc loc) {
    super(name, body, annotation, loc);
    this.typeN = typeN;
  }

  public Optional<TypeP> typeN() {
    return typeN;
  }

  @Override
  public Optional<TypeP> evalT() {
    return typeN();
  }
}
