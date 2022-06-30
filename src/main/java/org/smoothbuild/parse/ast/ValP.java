package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;

public final class ValP extends MonoRefableP implements MonoTopRefableP {
  private final Optional<TypeP> typeP;

  public ValP(Optional<TypeP> typeP, String name, Optional<ObjP> body,
      Optional<AnnP> annotation, Loc loc) {
    super(name, body, annotation, loc);
    this.typeP = typeP;
  }

  public Optional<TypeP> typeP() {
    return typeP;
  }

  @Override
  public Optional<TypeP> evalT() {
    return typeP();
  }
}