package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;

public sealed abstract class GenericRefableP extends GenericNamedP implements RefableP
    permits FuncP {
  private final Optional<ObjP> body;
  private final Optional<AnnP> ann;

  public GenericRefableP(String name, Optional<ObjP> body, Optional<AnnP> ann, Loc loc) {
    super(name, loc);
    this.body = body;
    this.ann = ann;
  }

  @Override
  public Optional<ObjP> body() {
    return body;
  }

  @Override
  public Optional<AnnP> ann() {
    return ann;
  }

  @Override
  public final boolean equals(Object object) {
    return object instanceof GenericRefableP that
        && this.name().equals(that.name());
  }

  @Override
  public final int hashCode() {
    return name().hashCode();
  }

  @Override
  public String toString() {
    return "[" + name() + ":" + loc() + "]";
  }
}
